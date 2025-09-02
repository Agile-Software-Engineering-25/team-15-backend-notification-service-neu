package com.ase.userservice.service;

import com.ase.userservice.model.Notification;
import com.ase.userservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

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
