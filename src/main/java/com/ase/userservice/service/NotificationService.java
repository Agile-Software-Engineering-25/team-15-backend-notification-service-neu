
package com.ase.userservice.service;
import java.time.Instant;
import java.util.Optional;
import com.ase.userservice.DummyData;
import com.ase.userservice.config.RepositoryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ase.userservice.model.Notification;
import com.ase.userservice.repository.NotificationRepository;

/**
 * Service class for managing notification operations.
 */
@Service
@Slf4j
@EnableConfigurationProperties(RepositoryConfig.class)
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final RepositoryConfig repositoryConfig;

  /**
   * Creates a new NotificationService with the given repository.
   *
   * @param notificationRepository
   */
  public NotificationService(NotificationRepository notificationRepository, RepositoryConfig repositoryConfig) {
    this.notificationRepository = notificationRepository;
    this.repositoryConfig = repositoryConfig;
  }

  /**
   * Marks a notification as unread by setting its readAt timestamp to null.
   *
   * @param id the ID of the notification to mark as unread
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsUnread(Long id) {
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
  public boolean markAsRead(Long id) {
    Optional<Notification> notificationOpt
        = notificationRepository.findById(id);
    if (notificationOpt.isPresent()) {
      Notification notification = notificationOpt.get();
      notification.setReadAt(Instant.now());
      notificationRepository.save(notification);
      return true;
    }
    return false;
  }

  /**
   * Retrieves a notification and marks it as read in a single transaction.
   *
   * @param id the ID of the notification to retrieve and mark as read
   * @return an Optional containing the notification if found, empty otherwise
   */
  @Transactional
  public Optional<Notification> getAndMarkAsRead(Long id) {
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
    return notificationRepository.save(notification);
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
}
