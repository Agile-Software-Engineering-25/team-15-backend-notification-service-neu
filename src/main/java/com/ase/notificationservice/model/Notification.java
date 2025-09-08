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
  /**
   * The unique identifier for the notification.
   * -- GETTER --
   *  Gets the notification's ID.
   *
   *
   * -- SETTER --
   *  Sets the notification's ID.
   *
   @return the ID
    * @param id the ID to set

   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  /**
   * The content of the notification message.
   * -- GETTER --
   *  Gets the notification message.
   *
   *
   * -- SETTER --
   *  Sets the notification message.
   *
   @return the message
    * @param message the message to set

   */
  @Column
  private String message;

  /**
   * The timestamp when the notification was read, or null if unread.
   * -- GETTER --
   *  Gets the timestamp when the notification was read.
   *
   *
   * -- SETTER --
   *  Sets the timestamp when the notification was read.
   *
   @return the read timestamp, or null if unread
    * @param readAt the read timestamp to set, or null to mark as unread

   */
  @Column(name = "read_at")
  private Instant readAt;

    /**
   * The priority of the notification
   * -- GETTER --
   *  Gets the priority of the notification.
   *
   *
   * -- SETTER --
   *  Sets the priority of the notification.
   *
   @return the priority of the notification.
    * @param priority the priority to set

   */
  @Column
  private int priority;

  /**
   * The timestamp when the notification was recived.
   * -- GETTER --
   *  Gets the timestamp when the notification was recived.
   *
   *
   * -- SETTER --
   *  Sets the timestamp when the notification was recived.
   *
   @return the received timestamp, or null if
    * @param receivedAt the read timestamp to set, or null to mark as unread

   */
  @Column(name = "received_at")
  private Instant receivedAt;


    /**
   * The title of the notification
   * -- GETTER --
   *  Gets the title of the notification.
   *
   *
   * -- SETTER --
   *  Sets the title of the notification.
   *
   @return the title of the notification.
    * @param title the title to set

   */
  @Column
  private String title;


  /**
   * The description of the notification
   * -- GETTER --
   *  Gets the description of the notification.
   *
   *
   * -- SETTER --
   *  Sets  the description of the notification.
   *
   @return the description of the notification.
    * @param description the description to set

   */
  @Column
  private String description;

}


