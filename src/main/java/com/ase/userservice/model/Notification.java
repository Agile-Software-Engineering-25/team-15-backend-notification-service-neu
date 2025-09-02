package com.ase.userservice.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity class representing a notification in the system.
 */
@Entity
@Table(name = "notification")
public class Notification {
  /**
   * The unique identifier for the notification.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  /**
   * The content of the notification message.
   */
  @Column
  private String message;

  /**
   * The timestamp when the notification was read, or null if unread.
   */
  @Column
  private Instant readAt;

  /**
   * Gets the notification's ID.
   *
   * @return the ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the notification's ID.
   *
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the notification message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the notification message.
   *
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the timestamp when the notification was read.
   *
   * @return the read timestamp, or null if unread
   */
  public Instant getReadAt() {
    return readAt;
  }

  /**
   * Sets the timestamp when the notification was read.
   *
   * @param readAt the read timestamp to set, or null to mark as unread
   */
  public void setReadAt(Instant readAt) {
    this.readAt = readAt;
  }
}
