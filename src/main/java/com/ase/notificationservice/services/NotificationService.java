package com.ase.notificationservice.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;
import com.ase.notificationservice.DummyData;
import com.ase.notificationservice.components.GetToken;
import com.ase.notificationservice.config.RepositoryConfig;
import com.ase.notificationservice.config.UserServiceConfig;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.enums.EmailTemplate;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import com.ase.notificationservice.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing notification operations.
 */
@Service
@Slf4j
@EnableConfigurationProperties({RepositoryConfig.class, UserServiceConfig.class})
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final RepositoryConfig repositoryConfig;
  private final UserServiceConfig userServiceConfig;
  private final SimpMessagingTemplate messagingTemplate;
  private final EmailService emailService;
  private final GetToken getToken;
  private WebClient userClient;

  @jakarta.annotation.PostConstruct
  void init() {
    userClient = WebClient.builder()
        .baseUrl(userServiceConfig.getUrl())
        .build();
  }

  /**
   * Marks a notification as unread by setting its readAt timestamp to null.
   *
   * @param id the ID of the notification to mark as unread
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsUnread(final String id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    if (notificationOpt.isPresent()) {
      Notification notification
          = notificationOpt.get();
      notification.setReadAt(null);
      notificationRepository.save(notification);
      return true;
    }
    return false;
  }

  /**
   * Marks a notification as read
   * by setting its readAt timestamp to the current time.
   *
   * @param id id of the notification
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsRead(final String id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    if (notificationOpt.isEmpty()) {
      return false;
    }
    Notification notification = notificationOpt.get();
    notification.setReadAt(Instant.now());
    notificationRepository.save(notification);
    return true;
  }

  /**
   * Retrieves a notification and marks it as read in a single transaction.
   *
   * @param id the ID of the notification to retrieve and mark as read
   * @return an Optional containing the notification if found, empty otherwise
   */
  @Transactional
  public Optional<Notification> getAndMarkAsRead(final String id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    notificationOpt.ifPresent(notification -> {
      notification.setReadAt(Instant.now());
      notificationRepository.save(notification);
    });
    return notificationOpt;
  }

  /**
   * Retrieves a notification and marks it as unread in a single transaction.
   *
   * @param id the ID of the notification to retrieve and mark as read
   * @return an Optional containing the notification if found, empty otherwise
   */
  @Transactional
  public Optional<Notification> getAndMarkAsUnread(String id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    notificationOpt.ifPresent(notification -> {
      notification.setReadAt(null);
      notificationRepository.save(notification);
    });
    return notificationOpt;
  }

  /**
   * Creates and publishes a notification.
   *
   * @param notification the notification to create
   * @return the saved notification
   */
  @Transactional
  public Notification createNotification(final Notification notification,
                                         final Optional<EmailTemplate> emailTemplate,
                                         final Optional<Map<String, Object>> variables) {
    log.info("Notification to publish: {}", notification);
    Notification saved = notificationRepository.save(notification);

    messagingTemplate.convertAndSend(
        "/topic/notifications/" + notification.getUserId(),
        notificationRepository.findByUserId(notification.getUserId()));

    if (shouldSendMail(saved)) {
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            sendEmailInline(saved, emailTemplate, variables);
          }
        });
      }
      else {
        sendEmailInline(saved, emailTemplate, variables);
      }
    }
    return saved;
  }

  /**
   * Initializes dummy data on application startup.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void instantiateDummies() {
    if (!repositoryConfig.isInitializeWithDummyData()) {
      log.info("Skipping dummy data insertion");
      return;
    }

    log.info("Inserting {} dummy notifications",
        DummyData.NOTIFICATIONS.size());
    DummyData.NOTIFICATIONS.forEach(n ->
        createNotification(n, Optional.empty(), Optional.empty()));
  }

  /**
   * Retrieves all notifications for a specific user.
   *
   * @param userId the user ID
   * @return list of notifications
   */
  public List<Notification> getNotificationsForUser(final String userId) {
    return notificationRepository.findByUserId(userId);
  }

  private void sendEmailInline(
      Notification notification,
      Optional<EmailTemplate> emailTemplateOptional,
      Optional<Map<String, Object>> variablesOptional) {

    String email = fetchUserEmail(notification.getUserId())
        .filter(s -> !s.isBlank())
        .orElseThrow(() ->
            new IllegalStateException("No email found for userId=" + notification.getUserId()));

    EmailTemplate chosenTemplate = emailTemplateOptional.orElseGet(() ->
        resolveTemplate(notification));

    Map<String, Object> defaults = buildDefaultVariables(notification);
    Map<String, Object> vars = new java.util.HashMap<>(defaults);
    variablesOptional.ifPresent(vars::putAll);

    if (vars.isEmpty()) {
      vars = null;
    }

    String subject = java.util.Optional.ofNullable(notification.getTitle()).orElse("Notification");
    if (notification.isPriority()) {
      subject = "[PRIORITY] " + subject;
    }

    EmailNotificationRequestDto req = EmailNotificationRequestDto.builder()
        .to(java.util.List.of(email))
        .subject(subject)
        .text(java.util.Optional.ofNullable(notification.getMessage()).orElse(""))
        .template(chosenTemplate)
        .variables(vars)
        .build();

    try {
      emailService.sendEmail(req);
      log.info("Sent email for notification {}", notification.getId());
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to send email for " + notification.getId(), e);
    }
  }

  private Optional<String> fetchUserEmail(String userId) {
    try {
      String token = getToken.getToken();
      
      record PersonDetailsDto(
          String id,
          String dateOfBirth,
          String address,
          String phoneNumber,
          String photoUrl,
          String username,
          String firstName,
          String lastName,
          String email
      ) {
      }
      
      PersonDetailsDto resp = userClient.get()
          .uri("/users/{id}", userId)
          .header("Authorization", "Bearer " + token)
          .retrieve()
          .bodyToMono(PersonDetailsDto.class)
          .timeout(Duration.ofMillis(userServiceConfig.getTimeoutMs()))
          .block();
      return Optional.ofNullable(resp != null ? resp.email() : null);
    }
    catch (Exception e) {
      log.warn("UserService lookup failed for {}: {}", userId, e.toString());
      return Optional.empty();
    }
  }

  private boolean shouldSendMail(Notification n) {
    return Objects.equals(n.getNotifyType(), NotifyType.Mail)
        || Objects.equals(n.getNotifyType(), NotifyType.All);
  }

  /**
   * Fetches user IDs from a group via the user service API.
   * Uses the Group API endpoint: GET /api/v1/group/{groupName}
   * Requires JWT authentication.
   *
   * @param groupName the group name (e.g., cohort name)
   * @return list of user UUIDs in the group
   * @throws IllegalStateException if group notifications are disabled
   * @throws RuntimeException      if the group service request fails
   */
  public List<String> getUsersInGroup(final String groupName) {
    if (!userServiceConfig.isGroupsEnabled()) {
      throw new IllegalStateException(
          "Group notifications are disabled. Group name: " + groupName);
    }

    try {
      // Get JWT token for authentication
      String token = getToken.getToken();
      
      record StudentDto(String uuid) {
      }
      record GroupDto(String name, Integer students_count, java.util.List<StudentDto> students) {
      }

      GroupDto groupDto = userClient.get()
          .uri("/group/{groupName}", groupName)
          .header("Authorization", "Bearer " + token)
          .retrieve()
          .bodyToMono(GroupDto.class)
          .timeout(Duration.ofMillis(userServiceConfig.getTimeoutMs()))
          .block();

      if (groupDto == null || groupDto.students() == null) {
        log.warn("Group '{}' not found or has no students", groupName);
        return java.util.Collections.emptyList();
      }

      return groupDto.students().stream()
          .map(StudentDto::uuid)
          .filter(java.util.Objects::nonNull)
          .toList();
    }
    catch (Exception e) {
      log.error("Error fetching users for group '{}': {}", groupName, e.getMessage());
      throw new RuntimeException(
          "Failed to fetch users for group " + groupName + ": " + e.getMessage(),
          e);
    }
  }

  private EmailTemplate resolveTemplate(Notification n) {
    return EmailTemplate.GENERIC;
  }

  private Map<String, Object> buildDefaultVariables(Notification n) {
    Map<String, Object> vars = new java.util.HashMap<>();

    String fallbackHeader = java.util.Optional.ofNullable(n.getTitle()).orElseGet(() -> {
      return switch (n.getNotificationType()) {
        case Warning -> "Systemhinweis";
        case Congratulation -> "Glückwunsch!";
        case Info -> "Information";
        case None -> "Benachrichtigung";
      };
    });
    vars.put("header", fallbackHeader);

    if (n.getShortDescription() != null && !n.getShortDescription().isBlank()) {
      vars.put("preheader", n.getShortDescription());
    }

    if (n.getMessage() != null && !n.getMessage().isBlank()) {
      java.util.List<String> paragraphs = java.util.Arrays
          .stream(n.getMessage().trim().split("\\n\\s*\\n"))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .toList();
      if (!paragraphs.isEmpty()) {
        vars.put("body", paragraphs);
      }
    }

    if (n.getNotificationType() == NotificationType.Warning) {
      vars.put("note", "Wichtige Mitteilung.");
    }

    vars.putIfAbsent("footer", "SAU (Student Assistance Utilities)");

    return vars;
  }
}
