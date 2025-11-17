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
  void builderWithAllFieldsShouldCreateDto() {
    // Arrange
    final int age = 30;
    List<String> recipients = List.of("test1@example.com", "test2@example.com");
    Map<String, Object> variables = Map.of("name", "John", "age", age);

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
  void builderWithMinimalFieldsShouldCreateDto() {
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
  void builderWithTemplateOnlyShouldCreateDto() {
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
  void builderWithMultipleRecipientsShouldCreateDto() {
    // Arrange
    final int expectedSize = 4;
    List<String> recipients = List.of(
        "user1@example.com",
        "user2@example.com",
        "user3@example.com",
        "user4@example.com");

    // Act
    EmailNotificationRequestDto dto = EmailNotificationRequestDto.builder()
        .to(recipients)
        .subject("Bulk Email")
        .text("This is a bulk email")
        .build();

    // Assert
    assertThat(dto.to()).hasSize(expectedSize);
    assertThat(dto.to()).containsExactlyElementsOf(recipients);
  }

  @Test
  void builderWithComplexVariablesShouldCreateDto() {
    // Arrange
    final int userId = 123;
    final double totalPrice = 99.99;
    Map<String, Object> complexVariables = Map.of(
        "user", Map.of("name", "John Doe", "id", userId),
        "items", List.of("item1", "item2", "item3"),
        "total", totalPrice,
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
    assertThat(dto.variables().get("user")).isEqualTo(
        Map.of("name", "John Doe", "id", userId));
    assertThat(dto.variables().get("items")).isEqualTo(List.of("item1", "item2", "item3"));
    assertThat(dto.variables().get("total")).isEqualTo(totalPrice);
    assertThat(dto.variables().get("isVip")).isEqualTo(true);
  }

  @Test
  void equalsWithSameValuesShouldReturnTrue() {
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
  void equalsWithDifferentValuesShouldReturnFalse() {
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
  void hashCodeWithSameValuesShouldReturnSameHash() {
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
  void toStringShouldIncludeAllFields() {
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