package com.ase.notificationservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.ase.notificationservice.services.EmailService;

/**
 * Unit tests for EmailController without security.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
@WithMockUser(authorities = "ROLE_AREA-4.TEAM-15.WRITE.SENDNOTIFICATION")
class EmailControllerNoSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmailService emailService;

  @Test
  void sendEmail_withValidRequest_shouldReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(emailService).sendEmail(any());

    String requestBody = """
        {
          "to": ["test@example.com"],
          "subject": "Test Subject",
          "templateName": "generic-template",
          "templateData": {
            "name": "Test User",
            "message": "Test message"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNoContent()); // 204
  }

  @Test
  void sendEmail_withInvalidRequest_shouldReturnBadRequest() throws Exception {
    // Arrange - Missing required field
    String requestBody = """
        {
          "subject": "Test Subject"
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void sendEmail_withServiceException_shouldReturnInternalServerError() throws Exception {
    // Arrange
    doThrow(new RuntimeException("Service error")).when(emailService).sendEmail(any());

    String requestBody = """
        {
          "to": ["test@example.com"],
          "subject": "Test Subject",
          "templateName": "generic-template",
          "templateData": {
            "name": "Test User"
          }
        }
        """;

    // Act & Assert
    mockMvc.perform(post("/emails")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isInternalServerError());
  }
}