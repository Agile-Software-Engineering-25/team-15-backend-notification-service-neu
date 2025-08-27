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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        notification.setMessage("Test");
        notification.setReadAt(null);
        notification = notificationRepository.save(notification);
    }

    @Test
    void markAsRead_shouldSetReadAt() throws Exception {
        mockMvc.perform(post("/api/notifications/" + notification.getId() + "/mark-as-read")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
        assertThat(updated.getReadAt()).isNotNull();
        assertThat(updated.getReadAt()).isAfterOrEqualTo(Instant.now().minusSeconds(5));
    }
}
