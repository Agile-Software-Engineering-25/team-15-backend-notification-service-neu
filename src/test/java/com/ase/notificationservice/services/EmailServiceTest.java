package com.ase.notificationservice.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import com.ase.notificationservice.enums.EmailTemplate;

/**
 * Unit tests for EmailService.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private TemplateEngine templateEngine;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(emailService, "fromAddress", "test@example.com");
    ReflectionTestUtils.setField(emailService, "fromName", "Test Service");
  }

  @Test
  void sendEmailWithValidRequestShouldSendSuccessfully()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    Map<String, Object> variables = new HashMap<>();
    variables.put("name", "John Doe");
    
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .text("Test content")
        .template(EmailTemplate.GENERIC)
        .variables(variables)
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("generic-template"), any(Context.class)))
        .thenReturn("<html><body>Test HTML</body></html>");

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessage);
    verify(templateEngine).process(eq("generic-template"), any(Context.class));
  }

  @Test
  void sendEmailWithTextOnlyShouldSendWithPreFormattedHtml()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .text("Plain text content")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessage);
    verify(templateEngine, never()).process(any(String.class), any(Context.class));
  }

  @Test
  void sendEmailWithEmptyRecipientsShouldThrowIllegalArgumentException() {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of())
        .subject("Test Subject")
        .text("Test content")
        .build();

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> emailService.sendEmail(request)
    );
    
    assert exception.getMessage().contains("No recipients provided");
  }

  @Test
  void sendEmailWithNullRecipientsShouldThrowIllegalArgumentException() {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(null)
        .subject("Test Subject")
        .text("Test content")
        .build();

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> emailService.sendEmail(request)
    );
    
    assert exception.getMessage().contains("No recipients provided");
  }

  @Test
  void sendEmailWithNoContentAndNoTemplateShouldThrowIllegalArgumentException() {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> emailService.sendEmail(request)
    );
    
    assert exception.getMessage().contains("no content");
  }

  @Test
  void sendEmailWithMultipleRecipientsShouldSendToEach()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient1@example.com", "recipient2@example.com"))
        .subject("Test Subject")
        .text("Test content")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(mailSender, times(2)).createMimeMessage();
    verify(mailSender, times(2)).send(mimeMessage);
  }

  @Test
  void sendEmailWithReplyToShouldSetReplyToAddress()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .text("Test content")
        .replyTo("noreply@example.com")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(mailSender).createMimeMessage();
    verify(mailSender).send(mimeMessage);
  }

  @Test
  void sendEmailWithMailAuthenticationExceptionShouldPropagateException()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .text("Test content")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new MailAuthenticationException("Authentication failed"))
        .when(mailSender).send(any(MimeMessage.class));

    // Act & Assert
    assertThrows(MailAuthenticationException.class, () -> emailService.sendEmail(request));
  }

  @Test
  void sendEmailWithMessagingExceptionShouldPropagateException() {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .text("Test content")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new RuntimeException("Mail server error"))
        .when(mailSender).send(any(MimeMessage.class));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> emailService.sendEmail(request));
  }

  @Test
  void sendEmailWithCtaLinkShouldIncludeInVariables()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Test Subject")
        .template(EmailTemplate.GENERIC)
        .ctaLink("https://example.com/action")
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("generic-template"), any(Context.class)))
        .thenReturn("<html><body>Test HTML</body></html>");

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(templateEngine).process(eq("generic-template"), any(Context.class));
  }

  @Test
  void sendEmailWithPasswordTemplateShouldUseCorrectTemplate()
      throws MessagingException, UnsupportedEncodingException {
    // Arrange
    EmailNotificationRequestDto request = EmailNotificationRequestDto.builder()
        .to(List.of("recipient@example.com"))
        .subject("Password Reset")
        .template(EmailTemplate.RESET_PASSWORD)
        .build();

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("password"), any(Context.class)))
        .thenReturn("<html><body>Password Reset</body></html>");

    // Act
    emailService.sendEmail(request);

    // Assert
    verify(templateEngine).process(eq("password"), any(Context.class));
  }
}