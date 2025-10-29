package com.ase.notificationservice.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import com.ase.notificationservice.repositories.NotificationRepository;

/**
 * Integration tests for the notification workflow.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    notificationRepository.deleteAll();
  }

  @Test
  void createAndRetrieveNotification_shouldWorkEndToEnd() throws Exception {
    // Arrange - Create a notification directly in the repository
    Notification notification = Notification.builder()
        .userId("integration-user")
        .message("Integration test message")
        .title("Integration Test")
        .shortDescription("This is an integration test")
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .receivedAt(Instant.now())
        .build();

    Notification saved = notificationRepository.save(notification);

    // Act & Assert - Retrieve notifications for user
    mockMvc.perform(get("/notifications")
            .param("userId", "integration-user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(saved.getId()))
        .andExpect(jsonPath("$[0].userId").value("integration-user"))
        .andExpect(jsonPath("$[0].message").value("Integration test message"))
        .andExpect(jsonPath("$[0].title").value("Integration Test"))
        .andExpect(jsonPath("$[0].readAt").isEmpty());
  }

  @Test
  void markNotificationAsRead_shouldUpdateDatabase() throws Exception {
    // Arrange - Create a notification
    Notification notification = Notification.builder()
        .userId("read-test-user")
        .message("Test message for reading")
        .title("Read Test")
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .receivedAt(Instant.now())
        .build();

    Notification saved = notificationRepository.save(notification);
    assertThat(saved.getReadAt()).isNull();

    // Act - Mark as read
    mockMvc.perform(post("/notifications/mark-as-read/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.readAt").isNotEmpty());

    // Assert - Verify in database
    Notification updated = notificationRepository.findById(saved.getId()).orElseThrow();
    assertThat(updated.getReadAt()).isNotNull();
    assertThat(updated.getReadAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void markNotificationAsUnread_shouldClearReadAt() throws Exception {
    // Arrange - Create a notification and mark it as read
    Notification notification = Notification.builder()
        .userId("unread-test-user")
        .message("Test message for unreading")
        .title("Unread Test")
        .readAt(Instant.now())
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .receivedAt(Instant.now())
        .build();

    Notification saved = notificationRepository.save(notification);
    assertThat(saved.getReadAt()).isNotNull();

    // Act - Mark as unread
    mockMvc.perform(post("/notifications/mark-as-unread/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.readAt").isEmpty());

    // Assert - Verify in database
    Notification updated = notificationRepository.findById(saved.getId()).orElseThrow();
    assertThat(updated.getReadAt()).isNull();
  }

  @Test
  void getNotificationsForUser_withMultipleNotifications_shouldReturnAll() throws Exception {
    // Arrange - Create multiple notifications for the same user
    String userId = "multi-notification-user";

    Notification notification1 = Notification.builder()
        .userId(userId)
        .message("First notification")
        .title("Notification 1")
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .receivedAt(Instant.now().minusSeconds(120))
        .build();

    Notification notification2 = Notification.builder()
        .userId(userId)
        .message("Second notification")
        .title("Notification 2")
        .notifyType(NotifyType.Mail)
        .notificationType(NotificationType.Warning)
        .receivedAt(Instant.now().minusSeconds(60))
        .build();

    Notification notification3 = Notification.builder()
        .userId(userId)
        .message("Third notification")
        .title("Notification 3")
        .notifyType(NotifyType.All)
        .notificationType(NotificationType.Congratulation)
        .receivedAt(Instant.now())
        .readAt(Instant.now())
        .build();

    notificationRepository.saveAll(List.of(notification1, notification2, notification3));

    // Act & Assert
    mockMvc.perform(get("/notifications")
            .param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[?(@.title == 'Notification 1')]").exists())
        .andExpect(jsonPath("$[?(@.title == 'Notification 2')]").exists())
        .andExpect(jsonPath("$[?(@.title == 'Notification 3')]").exists());
  }

  @Test
  void getNotificationsForUser_withNoNotifications_shouldReturnEmptyArray() throws Exception {
    // Act & Assert
    mockMvc.perform(get("/notifications")
            .param("userId", "non-existent-user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void markNonExistentNotificationAsRead_shouldReturnNotFound() throws Exception {
    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-read/non-existent-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  void markNonExistentNotificationAsUnread_shouldReturnNotFound() throws Exception {
    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-unread/non-existent-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  void notificationPersistence_shouldMaintainAllFields() throws Exception {
    // Arrange - Create a comprehensive notification
    Notification comprehensive = Notification.builder()
        .userId("comprehensive-user")
        .message("Comprehensive test message with special characters: !@#$%^&*()")
        .title("Comprehensive Test Title")
        .shortDescription("A comprehensive test description")
        .priority(true)
        .notifyType(NotifyType.All)
        .notificationType(NotificationType.Warning)
        .receivedAt(Instant.now())
        .build();

    Notification saved = notificationRepository.save(comprehensive);

    // Act - Retrieve via API
    mockMvc.perform(get("/notifications")
            .param("userId", "comprehensive-user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(saved.getId()))
        .andExpect(jsonPath("$[0].userId").value("comprehensive-user"))
        .andExpect(jsonPath("$[0].message").value("Comprehensive test message with special characters: !@#$%^&*()"))
        .andExpect(jsonPath("$[0].title").value("Comprehensive Test Title"))
        .andExpect(jsonPath("$[0].shortDescription").value("A comprehensive test description"))
        .andExpect(jsonPath("$[0].priority").value(true))
        .andExpect(jsonPath("$[0].notifyType").value("All"))
        .andExpect(jsonPath("$[0].notificationType").value("Warning"));

    // Assert - Verify direct database access
    Notification retrieved = notificationRepository.findById(saved.getId()).orElseThrow();
    assertThat(retrieved.getUserId()).isEqualTo("comprehensive-user");
    assertThat(retrieved.getMessage()).contains("special characters");
    assertThat(retrieved.isPriority()).isTrue();
    assertThat(retrieved.getNotifyType()).isEqualTo(NotifyType.All);
    assertThat(retrieved.getNotificationType()).isEqualTo(NotificationType.Warning);
  }
}