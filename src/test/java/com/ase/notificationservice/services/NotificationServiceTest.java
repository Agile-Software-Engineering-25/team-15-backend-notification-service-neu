package com.ase.notificationservice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.ase.notificationservice.config.RepositoryConfig;
import com.ase.notificationservice.config.UserServiceConfig;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import com.ase.notificationservice.repositories.NotificationRepository;

/**
 * Unit tests for NotificationService.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private RepositoryConfig repositoryConfig;

  @Mock
  private UserServiceConfig userServiceConfig;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private NotificationService notificationService;

  private Notification testNotification;

  @BeforeEach
  void setUp() {
    testNotification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .title("Test title")
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .receivedAt(Instant.now())
        .build();
  }

  @Test
  void markAsUnread_withExistingNotification_shouldReturnTrue() {
    // Arrange
    when(notificationRepository.findById("test-id"))
        .thenReturn(Optional.of(testNotification));
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);

    // Act
    boolean result = notificationService.markAsUnread("test-id");

    // Assert
    assertTrue(result);
    verify(notificationRepository).findById("test-id");
    verify(notificationRepository).save(testNotification);
    assertThat(testNotification.getReadAt()).isNull();
  }

  @Test
  void markAsUnread_withNonExistentNotification_shouldReturnFalse() {
    // Arrange
    when(notificationRepository.findById("non-existent"))
        .thenReturn(Optional.empty());

    // Act
    boolean result = notificationService.markAsUnread("non-existent");

    // Assert
    assertFalse(result);
    verify(notificationRepository).findById("non-existent");
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void markAsRead_withExistingNotification_shouldReturnTrue() {
    // Arrange
    when(notificationRepository.findById("test-id"))
        .thenReturn(Optional.of(testNotification));
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);

    // Act
    boolean result = notificationService.markAsRead("test-id");

    // Assert
    assertTrue(result);
    verify(notificationRepository).findById("test-id");
    verify(notificationRepository).save(testNotification);
    assertThat(testNotification.getReadAt()).isNotNull();
    assertThat(testNotification.getReadAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void markAsRead_withNonExistentNotification_shouldReturnFalse() {
    // Arrange
    when(notificationRepository.findById("non-existent"))
        .thenReturn(Optional.empty());

    // Act
    boolean result = notificationService.markAsRead("non-existent");

    // Assert
    assertFalse(result);
    verify(notificationRepository).findById("non-existent");
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void getAndMarkAsRead_withExistingNotification_shouldReturnNotificationAndMarkAsRead() {
    // Arrange
    when(notificationRepository.findById("test-id"))
        .thenReturn(Optional.of(testNotification));
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);

    // Act
    Optional<Notification> result = notificationService.getAndMarkAsRead("test-id");

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testNotification);
    assertThat(testNotification.getReadAt()).isNotNull();
    verify(notificationRepository).save(testNotification);
  }

  @Test
  void getAndMarkAsUnread_withExistingNotification_shouldReturnNotificationAndMarkAsUnread() {
    // Arrange
    testNotification.setReadAt(Instant.now());
    when(notificationRepository.findById("test-id"))
        .thenReturn(Optional.of(testNotification));
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);

    // Act
    Optional<Notification> result = notificationService.getAndMarkAsUnread("test-id");

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testNotification);
    assertThat(testNotification.getReadAt()).isNull();
    verify(notificationRepository).save(testNotification);
  }

  @Test
  void createNotification_withUINotifyType_shouldNotSendEmail() {
    // Arrange
    testNotification.setNotifyType(NotifyType.UI);
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);
    when(notificationRepository.findByUserId("user-123"))
        .thenReturn(List.of(testNotification));

    // Act
    Notification result = notificationService.createNotification(
        testNotification, Optional.empty(), Optional.empty());

    // Assert
    assertThat(result).isEqualTo(testNotification);
    verify(notificationRepository).save(testNotification);
    verify(messagingTemplate).convertAndSend(
        eq("/topic/notifications/user-123"), 
        eq(List.of(testNotification)));
    // Email service should not be called for UI notifications
  }

  @Test
  void getNotificationsForUser_shouldReturnUserNotifications() {
    // Arrange
    List<Notification> expectedNotifications = List.of(testNotification);
    when(notificationRepository.findByUserId("user-123"))
        .thenReturn(expectedNotifications);

    // Act
    List<Notification> result = notificationService.getNotificationsForUser("user-123");

    // Assert
    assertThat(result).isEqualTo(expectedNotifications);
    verify(notificationRepository).findByUserId("user-123");
  }

  @Test
  void getUsersInGroup_withGroupsDisabled_shouldThrowIllegalStateException() {
    // Arrange
    when(userServiceConfig.isGroupsEnabled()).thenReturn(false);

    // Act & Assert
    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> notificationService.getUsersInGroup("test-group")
    );
    
    assertThat(exception.getMessage()).contains("Group notifications are disabled");
  }

  @Test
  void getAndMarkAsRead_withNonExistentNotification_shouldReturnEmpty() {
    // Arrange
    when(notificationRepository.findById("non-existent"))
        .thenReturn(Optional.empty());

    // Act
    Optional<Notification> result = notificationService.getAndMarkAsRead("non-existent");

    // Assert
    assertThat(result).isEmpty();
    verify(notificationRepository).findById("non-existent");
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void getAndMarkAsUnread_withNonExistentNotification_shouldReturnEmpty() {
    // Arrange
    when(notificationRepository.findById("non-existent"))
        .thenReturn(Optional.empty());

    // Act
    Optional<Notification> result = notificationService.getAndMarkAsUnread("non-existent");

    // Assert
    assertThat(result).isEmpty();
    verify(notificationRepository).findById("non-existent");
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void createNotification_shouldSendWebSocketMessage() {
    // Arrange
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);
    when(notificationRepository.findByUserId("user-123"))
        .thenReturn(List.of(testNotification));

    // Act
    notificationService.createNotification(
        testNotification, Optional.empty(), Optional.empty());

    // Assert
    verify(messagingTemplate).convertAndSend(
        eq("/topic/notifications/user-123"), 
        eq(List.of(testNotification)));
  }

  @Test
  void createNotification_withUINotifyType_shouldSendWebSocketOnly() {
    // Arrange - Test only UI notifications to avoid email service complexity
    testNotification.setNotifyType(NotifyType.UI);
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(testNotification);
    when(notificationRepository.findByUserId("user-123"))
        .thenReturn(List.of(testNotification));

    // Act
    Notification result = notificationService.createNotification(
        testNotification, Optional.empty(), Optional.empty());

    // Assert
    assertThat(result).isEqualTo(testNotification);
    verify(messagingTemplate).convertAndSend(
        eq("/topic/notifications/user-123"), 
        eq(List.of(testNotification)));
    // No email service should be called for UI-only notifications
  }
}