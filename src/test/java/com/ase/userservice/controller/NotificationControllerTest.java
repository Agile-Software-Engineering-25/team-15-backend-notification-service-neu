package com.ase.userservice.controller;

import com.ase.userservice.model.Notification;
import com.ase.userservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        notification = new Notification();
        notification.setMessage("Test-Message");
        notification.setReadAt(null);
        notification = notificationRepository.save(notification);
    }

    @Test
    void getAndMarkAsRead_shouldReturnNotificationAndSetReadAt() throws Exception {
        mockMvc.perform(get("/api/notifications")
                .header("Authorization", "mock-token")
                .header("X-Notification-Id", notification.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(notification.getId()))
            .andExpect(jsonPath("$.message").value("Test-Message"));

        Notification updated = notificationRepository
        .findById(notification.getId()).orElseThrow();
        assertThat(updated.getReadAt()).isNotNull();
        assertThat(updated.getReadAt()).isAfterOrEqualTo(Instant.now().minusSeconds(5));
    }

    @Test
    void getAndMarkAsRead_shouldReturn401IfNoAuthHeader() throws Exception {
        mockMvc.perform(get("/api/notifications")
                .header("X-Notification-Id", notification.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAndMarkAsRead_shouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/api/notifications")
                .header("Authorization", "mock-token")
                .header("X-Notification-Id", 99999L))
            .andExpect(status().isNotFound());
    }

    @Test
    void markAsRead_shouldSetReadAt() throws Exception {
        mockMvc.perform(post("/api/notifications/mark-as-read")
                .header("Authorization", "mock-token")
                .header("X-Notification-Id", notification.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Notification marked as read"));

        Notification updated = notificationRepository
        .findById(notification.getId()).orElseThrow();
        assertThat(updated.getReadAt()).isNotNull();
        assertThat(updated.getReadAt()).isAfterOrEqualTo(Instant.now().minusSeconds(5));
    }

    @Test
    void markAsUnread_shouldSetReadAtNull() throws Exception {
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);

        mockMvc.perform(post("/api/notifications/mark-as-unread")
                .header("Authorization", "mock-token")
                .header("X-Notification-Id", notification.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Notification marked as unread"));

        Notification updated = notificationRepository
        .findById(notification.getId()).orElseThrow();
        assertThat(updated.getReadAt()).isNull();
    }
}
