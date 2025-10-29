package com.ase.notificationservice.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import com.ase.notificationservice.services.NotificationService;

/**
 * Unit tests for NotificationController.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
class NotificationControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
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
  void getNotifications_withValidUserId_shouldReturnNotifications() throws Exception {
    // Arrange
    List<Notification> notifications = List.of(testNotification);
    when(notificationService.getNotificationsForUser("user-123"))
        .thenReturn(notifications);

    // Act & Assert
    mockMvc.perform(get("/notifications")
            .param("userId", "user-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("test-id")))
        .andExpect(jsonPath("$[0].userId", is("user-123")));

    verify(notificationService).getNotificationsForUser("user-123");
  }

  @Test
  void markAsRead_withExistingNotification_shouldReturnNotification() throws Exception {
    // Arrange
    when(notificationService.getAndMarkAsRead("test-id"))
        .thenReturn(Optional.of(testNotification));

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-read/test-id"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("test-id")))
        .andExpect(jsonPath("$.userId", is("user-123")));

    verify(notificationService).getAndMarkAsRead("test-id");
  }

  @Test
  void markAsRead_withNonExistentNotification_shouldReturnNotFound() throws Exception {
    // Arrange
    when(notificationService.getAndMarkAsRead("non-existent"))
        .thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-read/non-existent"))
        .andExpect(status().isNotFound());

    verify(notificationService).getAndMarkAsRead("non-existent");
  }

  @Test
  void markAsUnread_withExistingNotification_shouldReturnNotification() throws Exception {
    // Arrange
    when(notificationService.getAndMarkAsUnread("test-id"))
        .thenReturn(Optional.of(testNotification));

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-unread/test-id"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("test-id")))
        .andExpect(jsonPath("$.userId", is("user-123")));

    verify(notificationService).getAndMarkAsUnread("test-id");
  }

  @Test
  void markAsUnread_withNonExistentNotification_shouldReturnNotFound() throws Exception {
    // Arrange
    when(notificationService.getAndMarkAsUnread("non-existent"))
        .thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-unread/non-existent"))
        .andExpect(status().isNotFound());

    verify(notificationService).getAndMarkAsUnread("non-existent");
  }

  @Test
  void getNotifications_withEmptyResult_shouldReturnEmptyArray() throws Exception {
    // Arrange
    when(notificationService.getNotificationsForUser("user-456"))
        .thenReturn(List.of());

    // Act & Assert
    mockMvc.perform(get("/notifications")
            .param("userId", "user-456"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    verify(notificationService).getNotificationsForUser("user-456");
  }

  @Test
  void getNotifications_withMultipleNotifications_shouldReturnAll() throws Exception {
    // Arrange
    Notification notification2 = Notification.builder()
        .id("test-id-2")
        .userId("user-123")
        .message("Second message")
        .title("Second title")
        .notifyType(NotifyType.Mail)
        .notificationType(NotificationType.Warning)
        .receivedAt(Instant.now().minusSeconds(60))
        .build();

    List<Notification> notifications = List.of(testNotification, notification2);
    when(notificationService.getNotificationsForUser("user-123"))
        .thenReturn(notifications);

    // Act & Assert
    mockMvc.perform(get("/notifications")
            .param("userId", "user-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is("test-id")))
        .andExpect(jsonPath("$[1].id", is("test-id-2")));

    verify(notificationService).getNotificationsForUser("user-123");
  }
}