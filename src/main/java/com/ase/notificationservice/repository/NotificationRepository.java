package com.ase.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ase.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Repository interface for managing Notification entities.
 * Provides standard JPA CRUD operations for notifications.
 */
public interface NotificationRepository
    extends JpaRepository<Notification, String> {

  @Query("SELECT n FROM Notification n WHERE n.userId = ?1")
  List<Notification> findByUserId(String strings);
}
