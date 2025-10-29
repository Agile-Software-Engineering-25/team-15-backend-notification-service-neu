package com.ase.notificationservice.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ase.notificationservice.enums.EmailTemplate;

/**
 * Unit tests for EmailNotificationRequestDto.
 */
class EmailNotificationRequestDtoTest {

  @Test
  void builder_withAllFields_shouldCreateDto() {
    // Arrange
    List<String> recipients = List.of("test1@example.com", "test2@example.com");
    Map<String, Object> variables = Map.of("name", "John", "age", 30);

    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(recipients)
        .subject("Test Subject")
        .text("Test text content")
        .template(EmailTemplate.GENERIC)
        .variables(variables)
        .ctaLink("https://example.com/action")
        .replyTo("noreply@example.com")
        .build();

    // Assert
    assertThat(dto.to()).isEqualTo(recipients);
    assertThat(dto.subject()).isEqualTo("Test Subject");
    assertThat(dto.text()).isEqualTo("Test text content");
    assertThat(dto.template()).isEqualTo(EmailTemplate.GENERIC);
    assertThat(dto.variables()).isEqualTo(variables);
    assertThat(dto.ctaLink()).isEqualTo("https://example.com/action");
    assertThat(dto.replyTo()).isEqualTo("noreply@example.com");
  }

  @Test
  void builder_withMinimalFields_shouldCreateDto() {
    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(List.of("minimal@example.com"))
        .subject("Minimal Subject")
        .build();

    // Assert
    assertThat(dto.to()).containsExactly("minimal@example.com");
    assertThat(dto.subject()).isEqualTo("Minimal Subject");
    assertThat(dto.text()).isNull();
    assertThat(dto.template()).isNull();
    assertThat(dto.variables()).isNull();
    assertThat(dto.ctaLink()).isNull();
    assertThat(dto.replyTo()).isNull();
  }

  @Test
  void builder_withTemplateOnly_shouldCreateDto() {
    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(List.of("template@example.com"))
        .subject("Template Email")
        .template(EmailTemplate.RESET_PASSWORD)
        .build();

    // Assert
    assertThat(dto.to()).containsExactly("template@example.com");
    assertThat(dto.subject()).isEqualTo("Template Email");
    assertThat(dto.template()).isEqualTo(EmailTemplate.RESET_PASSWORD);
    assertThat(dto.text()).isNull();
  }

  @Test
  void builder_withMultipleRecipients_shouldCreateDto() {
    // Arrange
    List<String> recipients = List.of(
        "user1@example.com",
        "user2@example.com",
        "user3@example.com",
        "admin@example.com"
    );

    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(recipients)
        .subject("Bulk Email")
        .text("This is a bulk email")
        .build();

    // Assert
    assertThat(dto.to()).hasSize(4);
    assertThat(dto.to()).containsExactlyElementsOf(recipients);
  }

  @Test
  void builder_withComplexVariables_shouldCreateDto() {
    // Arrange
    Map<String, Object> complexVariables = Map.of(
        "user", Map.of("name", "John Doe", "id", 123),
        "items", List.of("item1", "item2", "item3"),
        "total", 99.99,
        "isVip", true
    );

    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(List.of("complex@example.com"))
        .subject("Complex Variables")
        .template(EmailTemplate.GENERIC)
        .variables(complexVariables)
        .build();

    // Assert
    assertThat(dto.variables()).isEqualTo(complexVariables);
    assertThat(dto.variables().get("user")).isEqualTo(Map.of("name", "John Doe", "id", 123));
    assertThat(dto.variables().get("items")).isEqualTo(List.of("item1", "item2", "item3"));
    assertThat(dto.variables().get("total")).isEqualTo(99.99);
    assertThat(dto.variables().get("isVip")).isEqualTo(true);
  }

  @Test
  void equals_withSameValues_shouldReturnTrue() {
    // Arrange
    EmailNotificationRequestDto dto1 = EmailNotificationRequestDto.builder()
        .to(List.of("test@example.com"))
        .subject("Same Subject")
        .text("Same text")
        .build();

    EmailNotificationRequestDto dto2 = EmailNotificationRequestDto.builder()
        .to(List.of("test@example.com"))
        .subject("Same Subject")
        .text("Same text")
        .build();

    // Act & Assert
    assertThat(dto1).isEqualTo(dto2);
  }

  @Test
  void equals_withDifferentValues_shouldReturnFalse() {
    // Arrange
    EmailNotificationRequestDto dto1 = EmailNotificationRequestDto.builder()
        .to(List.of("test1@example.com"))
        .subject("Subject 1")
        .build();

    EmailNotificationRequestDto dto2 = EmailNotificationRequestDto.builder()
        .to(List.of("test2@example.com"))
        .subject("Subject 2")
        .build();

    // Act & Assert
    assertThat(dto1).isNotEqualTo(dto2);
  }

  @Test
  void hashCode_withSameValues_shouldReturnSameHash() {
    // Arrange
    EmailNotificationRequestDto dto1 = EmailNotificationRequestDto.builder()
        .to(List.of("hash@example.com"))
        .subject("Hash Subject")
        .build();

    EmailNotificationRequestDto dto2 = EmailNotificationRequestDto.builder()
        .to(List.of("hash@example.com"))
        .subject("Hash Subject")
        .build();

    // Act & Assert
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  void toString_shouldIncludeAllFields() {
    // Arrange
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(List.of("toString@example.com"))
        .subject("ToString Subject")
        .text("ToString text")
        .template(EmailTemplate.GENERIC)
        .build();

    // Act
    String result = dto.toString();

    // Assert
    assertThat(result).contains("toString@example.com");
    assertThat(result).contains("ToString Subject");
    assertThat(result).contains("ToString text");
    assertThat(result).contains("GENERIC");
  }
}