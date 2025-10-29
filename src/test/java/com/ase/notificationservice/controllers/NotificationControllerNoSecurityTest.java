package com.ase.notificationservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
 * Unit tests for NotificationController without security.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
@SuppressWarnings("deprecation")
class NotificationControllerNoSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private NotificationService notificationService;

  @Test
  void getNotifications_withValidUserId_shouldReturnNotifications() throws Exception {
    // Arrange
    Notification notification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .title("Test title")
        .receivedAt(Instant.now())
        .notifyType(NotifyType.UI)
        .notificationType(NotificationType.Info)
        .build();
    
    when(notificationService.getNotificationsForUser(anyString())).thenReturn(List.of(notification));

    // Act & Assert
    mockMvc.perform(get("/notifications").param("userId", "user-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("test-id"));
  }

  @Test
  void getNotifications_withEmptyResult_shouldReturnEmptyArray() throws Exception {
    // Arrange
    when(notificationService.getNotificationsForUser(anyString())).thenReturn(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(get("/notifications").param("userId", "user-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void markAsRead_withExistingNotification_shouldReturnNotification() throws Exception {
    // Arrange
    Notification notification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .readAt(Instant.now())
        .build();
    
    when(notificationService.markAsRead(anyString())).thenReturn(true);
    when(notificationService.getAndMarkAsRead(anyString())).thenReturn(Optional.of(notification));

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-read/test-id"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("test-id"))
        .andExpect(jsonPath("$.readAt").isNotEmpty());
  }

  @Test
  void markAsUnread_withExistingNotification_shouldReturnNotification() throws Exception {
    // Arrange
    Notification notification = Notification.builder()
        .id("test-id")
        .userId("user-123")
        .message("Test message")
        .readAt(null)
        .build();
    
    when(notificationService.markAsUnread(anyString())).thenReturn(true);
    when(notificationService.getAndMarkAsUnread(anyString())).thenReturn(Optional.of(notification));

    // Act & Assert
    mockMvc.perform(post("/notifications/mark-as-unread/test-id"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("test-id"));
  }
}