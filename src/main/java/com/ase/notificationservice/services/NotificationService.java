package com.ase.notificationservice.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ase.notificationservice.config.UserServiceProperties;
import com.ase.notificationservice.config.RepositoryConfig;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import com.ase.notificationservice.config.UserServiceConfig;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.repositories.NotificationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private WebClient userClient;
  @Autowired
  private UserServiceProperties props;
  @jakarta.annotation.PostConstruct
  void init() {
    userClient = WebClient.builder()
        .baseUrl(props.getUrl())
        .build();
  }
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();

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
   * @param id
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
   * Creates and publishes a notification.
   *
   * @param notification the notification to create
   * @return the saved notification
   */
  @Transactional
  public Notification createNotification(final Notification notification) {
    log.info("Notification to publish: {}", notification);
    Notification saved = notificationRepository.save(notification);
    messagingTemplate.convertAndSend(
        "/topic/notifications/" + notification.getUserId(),
        notificationRepository.findByUserId(notification.getUserId()));
    if ("Mail".equalsIgnoreCase(saved.getNotificationType())
        || "All".equalsIgnoreCase(saved.getNotificationType())) {

      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override public void afterCommit() {
              sendEmailInline(saved);
            }
          }
      );
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
    DummyData.NOTIFICATIONS.forEach(this::createNotification);
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

  private void sendEmailInline(Notification n) {
    try {
      Optional<String> emailOpt = fetchUserEmail(n.getUserId());
      if (emailOpt.isEmpty() || emailOpt.get().isBlank()) {
        log.warn("No email found for userId={}, skipping email.", n.getUserId());
        return;
      }

      EmailNotificationRequestDto req = new EmailNotificationRequestDto(
          List.of(emailOpt.get()),
          n.getTitle() != null ? n.getTitle() : "Notification",
          n.getMessage() != null ? n.getMessage() : "",
          null, null, null, null
      );

      emailService.sendEmail(req);
      log.info("Sent email for notification {}", n.getId());
    } catch (Exception e) {
      log.error("Failed to send email for {}: {}", n.getId(), e.toString(), e);
    }
  }

  private Optional<String> fetchUserEmail(String userId) {
    try {
      record UserResp(String id, String email) {}
      UserResp resp = userClient.get()
          .uri("/users/{id}", userId)
          .retrieve()
          .bodyToMono(UserResp.class)
          .timeout(Duration.ofMillis(props.getTimeoutMs()))
          .block();
      return Optional.ofNullable(resp != null ? resp.email() : null);
    } catch (Exception e) {
      log.warn("UserService lookup failed for {}: {}", userId, e.toString());
      return Optional.empty();
    }
  }

  /**
   * Fetches user IDs from a group via the user service API.
   *
   * @param groupId the group ID
   * @return list of user IDs in the group
   * @throws IllegalStateException if group notifications are disabled
   * @throws RuntimeException if the group service request fails
   */
  public List<String> getUsersInGroup(final String groupId) {
    if (!userServiceConfig.isGroupsEnabled()) {
      throw new IllegalStateException(
          "Group notifications are disabled. Group ID: " + groupId);
    }

    try {
      // Change "/groups/getusers/" if the actual API endpoint is different
      String url = userServiceConfig.getUrl() + "/groups/getusers/" + groupId;

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());

      final int httpOk = 200;
      if (response.statusCode() == httpOk) {
        return parseUserIds(response.body());
      }
      throw new RuntimeException(
          "Failed to fetch users for group " + groupId
          + ": HTTP " + response.statusCode());
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(
          "Error fetching users for group " + groupId + ": " + e.getMessage(),
          e);
    }
  }

  private List<String> parseUserIds(final String jsonResponse) {
    List<String> userIds = new ArrayList<>();
    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);

      // Update parsing logic if API response format is different
      // Expected format: ["userId1", "userId2", "userId3"]
      if (rootNode.isArray()) {
        for (JsonNode element : rootNode) {
          if (element.isTextual()) {
            userIds.add(element.asText());
          }
        }
      }
    }
    catch (IOException e) {
      log.error("Error parsing user IDs from response: {}", e.getMessage());
    }
    return userIds;
  }
}
