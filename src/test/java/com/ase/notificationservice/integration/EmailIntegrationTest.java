package com.ase.notificationservice.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import com.ase.notificationservice.controllers.EmailController;
import com.ase.notificationservice.services.EmailService;

/**
 * Integration tests for the email workflow.
 */
@WebMvcTest(controllers = EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmailService emailService;

  @Test
  void sendEmail_withValidRequest_shouldReturnSuccess() throws Exception {
    // Arrange
    doNothing().when(emailService).sendEmail(any());

    String requestBody = """
        {
          "to": ["test@example.com"],
          "subject": "Integration Test Email",
          "template": "GENERIC",
          "variables": {
            "name": "Test User",
            "content": "This is an integration test email"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .with(user("testuser").authorities(new SimpleGrantedAuthority("ROLE_AREA-4.TEAM-15.WRITE.SENDNOTIFICATION")))
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNoContent());
  }

  @Test
  void sendEmail_withMissingFields_shouldReturnBadRequest() throws Exception {
    // Arrange - Missing required field (to)
    String requestBody = """
        {
          "subject": "Test Email",
          "template": "GENERIC"
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .with(user("testuser").authorities(new SimpleGrantedAuthority("ROLE_AREA-4.TEAM-15.WRITE.SENDNOTIFICATION")))
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void sendEmail_withMultipleRecipients_shouldReturnSuccess() throws Exception {
    // Arrange
    doNothing().when(emailService).sendEmail(any());

    String requestBody = """
        {
          "to": ["test1@example.com", "test2@example.com"],
          "subject": "Multi-recipient Test",
          "template": "GENERIC",
          "variables": {
            "name": "Multiple Users"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .with(user("testuser").authorities(new SimpleGrantedAuthority("ROLE_AREA-4.TEAM-15.WRITE.SENDNOTIFICATION")))
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNoContent());
  }
}