package com.ase.notificationservice.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the email workflow.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
class EmailIntegrationTestSimple {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("deprecation")
  private JavaMailSender javaMailSender;

  @Test
  void sendEmail_withValidRequest_shouldReturnSuccess() throws Exception {
    // Arrange
    doNothing().when(javaMailSender).send(any(MimeMessage.class));
    
    String requestBody = """
        {
          "to": "test@example.com",
          "subject": "Integration Test Email",
          "templateName": "generic-template",
          "templateData": {
            "name": "Test User",
            "content": "This is an integration test email"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Email sent successfully"));
  }

  @Test
  void sendEmail_withMissingFields_shouldReturnBadRequest() throws Exception {
    // Arrange - Missing required fields
    String requestBody = """
        {
          "subject": "Test Email",
          "templateName": "generic-template"
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void sendEmail_withMultipleRecipients_shouldReturnSuccess() throws Exception {
    // Arrange
    doNothing().when(javaMailSender).send(any(MimeMessage.class));
    
    String requestBody = """
        {
          "to": ["test1@example.com", "test2@example.com"],
          "subject": "Multi-recipient Test",
          "templateName": "generic-template",
          "templateData": {
            "name": "Multiple Users"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/email/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Email sent successfully"));
  }
}