package com.ase.notificationservice.services;

import java.time.Duration;
import java.time.Instant;
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
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing notification operations.
 */
@Service
@Slf4j
@EnableConfigurationProperties(RepositoryConfig.class)
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final RepositoryConfig repositoryConfig;
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
  /**
   * Marks a notification as unread by setting its readAt timestamp to null.
   *
   * @param id the ID of the notification to mark as unread
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsUnread(String id) {
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
  public boolean markAsRead(String id) {
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
  public Optional<Notification> getAndMarkAsRead(String id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    notificationOpt.ifPresent(notification -> {
      notification.setReadAt(Instant.now());
      notificationRepository.save(notification);
    });
    return notificationOpt;
  }

  @Transactional
  public Notification createNotification(Notification notification) {
    log.info("Notification to publish: {}", notification);
    Notification saved = notificationRepository.save(notification);
    messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(),
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

  @EventListener(ApplicationReadyEvent.class)
  public void instantiateDummies() {
    if (!repositoryConfig.isInitializeWithDummyData()) {
      log.info("Skipping dummy data insertion");
      return;
    }

    log.info("Inserting {} dummy notifications", DummyData.NOTIFICATIONS.size());
    DummyData.NOTIFICATIONS.forEach(this::createNotification);
  }

  public java.util.List<Notification> getNotificationsForUser(String userId) {
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
}
