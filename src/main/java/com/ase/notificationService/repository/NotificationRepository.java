package com.ase.notificationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ase.notificationService.model.Notification;

/**
 * Repository interface for managing Notification entities.
 * Provides standard JPA CRUD operations for notifications.
 */
public interface NotificationRepository
    extends JpaRepository<Notification, Long> {
}
