package com.ase.notificationservice.entities;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

/**
 * Entity class representing a notification in the system.
 */
@Setter
@Getter
@Entity
@Table(name = "notification")
@NoArgsConstructor
@SuperBuilder
@ToString
public class Notification {
  @Id
  @UuidGenerator
  @Column(length = 255, nullable = false, updatable = false)
  private String id;

  @Column
  private String userId;

  @Column
  private String message;

  @Column(name = "read_at")
  private Instant readAt;

  @Column
  private boolean priority;

  @Column(name = "received_at")
  private Instant receivedAt;

  @Column
  private String title;

  @Column
  private String shortDescription;

  @Column
  private NotifyType notifyType;

  @Enumerated(EnumType.STRING)
  @Column
  private NotificationType notificationType;
}


