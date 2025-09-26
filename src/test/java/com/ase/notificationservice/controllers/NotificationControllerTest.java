package com.ase.notificationservice.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.repositories.NotificationRepository;

/**
 * Integration tests for the NotificationController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    notification = Notification.builder().message("Test-Message").readAt(null).build();
    notification = notificationRepository.save(notification);
  }

  /**
   * Tests that getting a notification also marks it as read.
   */
  @Test
  void getAndMarkAsReadShouldReturnNotificationAndSetReadAt() throws Exception {
    mockMvc.perform(post("/notifications/mark-as-read")
        .header("X-Notification-Id", notification.getId())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    Notification updated = notificationRepository
        .findById(notification.getId())
        .orElseThrow();
    assertThat(updated.getReadAt()).isNotNull();
    assertThat(updated.getReadAt())
        .isAfterOrEqualTo(Instant.now().minusSeconds(RECENT_SECONDS));
  }

  /**
   * Tests that getting a non-existent notification returns 404.
   */
  @Test
  void getAndMarkAsReadShouldReturn404IfNotFound() throws Exception {
    mockMvc.perform(post("/notifications/mark-as-read")
    .header("X-Notification-Id", NON_EXISTENT_ID))
        .andExpect(status().isNotFound());
  }

  /**
   * Tests that marking a notification as read sets the readAt timestamp.
   */
  @Test
  void markAsReadShouldSetReadAt() throws Exception {
    mockMvc.perform(post("/notifications/mark-as-read")
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

    mockMvc.perform(post("/notifications/mark-as-unread")
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
