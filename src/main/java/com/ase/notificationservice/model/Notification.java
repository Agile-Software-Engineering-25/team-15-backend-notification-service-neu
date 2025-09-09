package com.ase.notificationservice.model;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column
  private String message;

  @Column(name = "read_at")
  private Instant readAt;

  @Column
  private int priority;

  @Column(name = "received_at")
  private Instant receivedAt;

  @Column
  private String title;

  @Column
  private String description;

}


