package com.ase.userservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.ase.userservice.model.Notification;
import com.ase.userservice.repository.NotificationRepository;

/**
 * Integration tests for the NotificationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {
  private static final int RECENT_SECONDS = 5;
  private static final long NON_EXISTENT_ID = 99999L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;

  private Notification notification;

  /**
   * Sets up test data before each test.
   * Creates a test notification and saves it to the repository.
   */
  @BeforeEach
  void setUp() {
    notificationRepository.deleteAll();
    notification = new Notification();
    notification.setMessage("Test-Message");
    notification.setReadAt(null);
    notification = notificationRepository.save(notification);
  }

  /**
   * Tests that getting a notification also marks it as read.
   */
  @Test
  void getAndMarkAsReadshouldReturnNotificationAndSetReadAt() throws Exception {
    mockMvc.perform(get("/api/notifications")
        .header("Authorization", "mock-token")
        .header("X-Notification-Id", notification.getId())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(notification.getId()))
        .andExpect(jsonPath("$.message").value("Test-Message"));

    Notification updated = notificationRepository
        .findById(notification.getId())
        .orElseThrow();
    assertThat(updated.getReadAt()).isNotNull();
    assertThat(updated.getReadAt())
        .isAfterOrEqualTo(Instant.now().minusSeconds(RECENT_SECONDS));
  }

  /**
   * Tests that getting a notification without auth header returns 401.
   */
  @Test
  void getAndMarkAsReadshouldReturn401IfNoAuthHeader() throws Exception {
    mockMvc.perform(get("/api/notifications")
        .header("X-Notification-Id", notification.getId()))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Tests that getting a non-existent notification returns 404.
   */
  @Test
  void getAndMarkAsReadshouldReturn404IfNotFound() throws Exception {
    mockMvc.perform(get("/api/notifications")
    .header("Authorization", "mock-token")
    .header("X-Notification-Id", NON_EXISTENT_ID))
        .andExpect(status().isNotFound());
  }

  /**
   * Tests that marking a notification as read sets the readAt timestamp.
   */
  @Test
  void markAsReadshouldSetReadAt() throws Exception {
    mockMvc.perform(post("/api/notifications/mark-as-read")
        .header("Authorization", "mock-token")
        .header("X-Notification-Id", notification.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Notification marked as read"));

    Notification updated = notificationRepository
        .findById(notification.getId())
        .orElseThrow();
    assertThat(updated.getReadAt()).isNotNull();
    assertThat(updated.getReadAt())
        .isAfterOrEqualTo(Instant.now().minusSeconds(RECENT_SECONDS));
  }

  /**
   * Tests that marking a notification as unread clears the readAt timestamp.
   */
  @Test
  void markAsUnreadShouldSetReadAtNull() throws Exception {
    notification.setReadAt(Instant.now());
    notificationRepository.save(notification);

    mockMvc.perform(post("/api/notifications/mark-as-unread")
        .header("Authorization", "mock-token")
        .header("X-Notification-Id", notification.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Notification marked as unread"));

    Notification updated = notificationRepository
        .findById(notification.getId())
        .orElseThrow();
    assertThat(updated.getReadAt()).isNull();
  }
}
