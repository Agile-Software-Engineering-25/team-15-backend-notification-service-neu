package com.ase.notificationservice.entities;

import java.time.Instant;
import com.ase.notificationservice.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column
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
  private NotificationType notificationType;
}


