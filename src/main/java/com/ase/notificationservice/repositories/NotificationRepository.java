package com.ase.notificationservice.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.ase.notificationservice.entities.Notification;

/**
 * Repository interface for managing Notification entities.
 * Provides standard JPA CRUD operations for notifications.
 */
public interface NotificationRepository
    extends JpaRepository<Notification, String> {

  @Query("SELECT n FROM Notification n WHERE n.userId = ?1")
  List<Notification> findByUserId(String strings);
}
