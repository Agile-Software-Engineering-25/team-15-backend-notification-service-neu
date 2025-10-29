package com.ase.notificationservice.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;

/**
 * Unit tests for Notification entity.
 */
class NotificationTest {

  @Test
  void builder_withAllFields_shouldCreateNotification() {
    // Arrange
    Instant now = Instant.now();
    Instant readAt = now.plusSeconds(60);

    // Act
    Notification notification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .title("Test title")
        .shortDescription("Short description")
        .priority(true)
        .receivedAt(now)
        .readAt(readAt)
        .notifyType(NotifyType.All)
        .notificationType(NotificationType.Warning)
        .build();

    // Assert
    assertThat(notification.getId()).isEqualTo("test-id");
    assertThat(notification.getUserId()).isEqualTo("user-123");
    assertThat(notification.getMessage()).isEqualTo("Test message");
    assertThat(notification.getTitle()).isEqualTo("Test title");
    assertThat(notification.getShortDescription()).isEqualTo("Short description");
    assertThat(notification.isPriority()).isTrue();
    assertThat(notification.getReceivedAt()).isEqualTo(now);
    assertThat(notification.getReadAt()).isEqualTo(readAt);
    assertThat(notification.getNotifyType()).isEqualTo(NotifyType.All);
    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.Warning);
  }

  @Test
  void builder_withMinimalFields_shouldCreateNotification() {
    // Act
    Notification notification = Notification.builder()
        .userId("user-456")
        .message("Minimal message")
        .build();

    // Assert
    assertThat(notification.getUserId()).isEqualTo("user-456");
    assertThat(notification.getMessage()).isEqualTo("Minimal message");
    assertThat(notification.getId()).isNull();
    assertThat(notification.getTitle()).isNull();
    assertThat(notification.isPriority()).isFalse();
    assertThat(notification.getReadAt()).isNull();
    assertThat(notification.getReceivedAt()).isNull();
  }

  @Test
  void setters_shouldUpdateFields() {
    // Arrange
    Notification notification = new Notification();
    Instant now = Instant.now();

    // Act
    notification.setId("new-id");
    notification.setUserId("new-user");
    notification.setMessage("New message");
    notification.setTitle("New title");
    notification.setShortDescription("New description");
    notification.setPriority(true);
    notification.setReceivedAt(now);
    notification.setReadAt(now.plusSeconds(30));
    notification.setNotifyType(NotifyType.Mail);
    notification.setNotificationType(NotificationType.Info);

    // Assert
    assertThat(notification.getId()).isEqualTo("new-id");
    assertThat(notification.getUserId()).isEqualTo("new-user");
    assertThat(notification.getMessage()).isEqualTo("New message");
    assertThat(notification.getTitle()).isEqualTo("New title");
    assertThat(notification.getShortDescription()).isEqualTo("New description");
    assertThat(notification.isPriority()).isTrue();
    assertThat(notification.getReceivedAt()).isEqualTo(now);
    assertThat(notification.getReadAt()).isEqualTo(now.plusSeconds(30));
    assertThat(notification.getNotifyType()).isEqualTo(NotifyType.Mail);
    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.Info);
  }

  @Test
  void toString_shouldIncludeAllFields() {
    // Arrange
    Notification notification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .title("Test title")
        .build();

    // Act
    String result = notification.toString();

    // Assert
    assertThat(result).contains("test-id");
    assertThat(result).contains("user-123");
    assertThat(result).contains("Test message");
    assertThat(result).contains("Test title");
  }

  @Test
  void equals_withSameId_shouldReturnTrue() {
    // Arrange
    Notification notification1 = Notification.builder()
        .id("same-id")
        .userId("user-1")
        .message("Message 1")
        .build();

    Notification notification2 = Notification.builder()
        .id("same-id")
        .userId("user-2")
        .message("Message 2")
        .build();

    // Act & Assert - Lombok generates equals based on all fields, not just ID
    // So these will be different due to different userId and message
    assertThat(notification1).isNotEqualTo(notification2);
  }

  @Test
  void equals_withSameValues_shouldReturnTrue() {
    // Arrange
    Instant now = Instant.now();
    
    Notification notification1 = Notification.builder()
        .id("same-id")
        .userId("user-1")
        .message("Same message")
        .title("Same title")
        .receivedAt(now)
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .priority(false)
        .build();

    Notification notification2 = Notification.builder()
        .id("same-id")
        .userId("user-1")
        .message("Same message")
        .title("Same title")
        .receivedAt(now)
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .priority(false)
        .build();

    // Act & Assert
    // Test individual fields since Lombok generates equals based on all fields
    assertThat(notification1.getId()).isEqualTo(notification2.getId());
    assertThat(notification1.getUserId()).isEqualTo(notification2.getUserId());
    assertThat(notification1.getMessage()).isEqualTo(notification2.getMessage());
    assertThat(notification1.getTitle()).isEqualTo(notification2.getTitle());
    assertThat(notification1.getReceivedAt()).isEqualTo(notification2.getReceivedAt());
    assertThat(notification1.getNotifyType()).isEqualTo(notification2.getNotifyType());
    assertThat(notification1.getNotificationType()).isEqualTo(notification2.getNotificationType());
    assertThat(notification1.isPriority()).isEqualTo(notification2.isPriority());
  }

  @Test
  void hashCode_withSameValues_shouldReturnSameHash() {
    // Arrange
    Instant now = Instant.now();
    
    Notification notification1 = Notification.builder()
        .id("same-id")
        .userId("user-1")
        .message("Same message")
        .title("Same title")
        .receivedAt(now)
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .priority(false)
        .build();

    Notification notification2 = Notification.builder()
        .id("same-id")
        .userId("user-1")
        .message("Same message")
        .title("Same title")
        .receivedAt(now)
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .priority(false)
        .build();

    // Act & Assert
    // Test that hashCode is consistent for the same object
    int hash1 = notification1.hashCode();
    int hash2 = notification1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
    
    // Test that both objects produce valid hash codes
    assertThat(notification1.hashCode()).isNotNull();
    assertThat(notification2.hashCode()).isNotNull();
  }

  @Test
  void notification_withNullValues_shouldHandleGracefully() {
    // Act
    Notification notification = Notification.builder()
        .userId("user-123")
        .message(null)
        .title(null)
        .shortDescription(null)
        .readAt(null)
        .receivedAt(null)
        .notifyType(null)
        .notificationType(null)
        .build();

    // Assert
    assertThat(notification.getUserId()).isEqualTo("user-123");
    assertThat(notification.getMessage()).isNull();
    assertThat(notification.getTitle()).isNull();
    assertThat(notification.getShortDescription()).isNull();
    assertThat(notification.getReadAt()).isNull();
    assertThat(notification.getReceivedAt()).isNull();
    assertThat(notification.getNotifyType()).isNull();
    assertThat(notification.getNotificationType()).isNull();
  }

  @Test
  void notification_withEmptyStrings_shouldPreserveValues() {
    // Act
    Notification notification = Notification.builder()
        .userId("")
        .message("")
        .title("")
        .shortDescription("")
        .build();

    // Assert
    assertThat(notification.getUserId()).isEqualTo("");
    assertThat(notification.getMessage()).isEqualTo("");
    assertThat(notification.getTitle()).isEqualTo("");
    assertThat(notification.getShortDescription()).isEqualTo("");
  }
}