package com.ase.userservice.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ase.userservice.model.Notification;
import com.ase.userservice.repository.NotificationRepository;

/**
 * Service class for managing notification operations.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  /**
   * Creates a new NotificationService with the given repository.
   *
   * @param notificationRepository the repository to use for notification operations
   */
  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  /**
   * Marks a notification as unread by setting its readAt timestamp to null.
   *
   * @param id the ID of the notification to mark as unread
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsUnread(Long id) {
    Optional<Notification> notificationOpt = notificationRepository.findById(id);
    if (notificationOpt.isPresent()) {
      Notification notification = notificationOpt.get();
      notification.setReadAt(null);
      notificationRepository.save(notification);
      return true;
    }
    return false;
  }

  /**
   * Marks a notification as read by setting its readAt timestamp to the current time.
   *
   * @param id the ID of the notification to mark as read
   * @return true if the notification was found and updated, false otherwise
   */
  @Transactional
  public boolean markAsRead(Long id) {
    Optional<Notification> notificationOpt = notificationRepository.findById(id);
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
    Optional<Notification> notificationOpt = notificationRepository.findById(id);
    notificationOpt.ifPresent(notification -> {
      notification.setReadAt(Instant.now());
      notificationRepository.save(notification);
    });
    return notificationOpt;
  }
}
