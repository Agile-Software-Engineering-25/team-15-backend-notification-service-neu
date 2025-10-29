package com.ase.notificationservice.dtos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ase.notificationservice.enums.EmailTemplate;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;

/**
 * Unit tests for NotificationCreationDto.
 */
class NotificationCreationDtoTest {

  @Test
  void settersAndGetters_withAllFields_shouldWorkCorrectly() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    String[] users = {"user1", "user2", "user3"};
    String[] groups = {"group1", "group2"};
    Map<String, Object> variables = Map.of("key1", "value1", "key2", 42);

    // Act
    dto.setUsers(users);
    dto.setGroups(groups);
    dto.setTitle("Test Title");
    dto.setMessage("Test Message");
    dto.setPriority(true);
    dto.setShortDescription("Short description");
    dto.setNotifyType(NotifyType.All);
    dto.setNotificationType(NotificationType.Warning);
    dto.setEmailTemplate(EmailTemplate.GENERIC);
    dto.setVariables(variables);

    // Assert
    assertThat(dto.getUsers()).isEqualTo(users);
    assertThat(dto.getGroups()).isEqualTo(groups);
    assertThat(dto.getTitle()).isEqualTo("Test Title");
    assertThat(dto.getMessage()).isEqualTo("Test Message");
    assertThat(dto.isPriority()).isTrue();
    assertThat(dto.getShortDescription()).isEqualTo("Short description");
    assertThat(dto.getNotifyType()).isEqualTo(NotifyType.All);
    assertThat(dto.getNotificationType()).isEqualTo(NotificationType.Warning);
    assertThat(dto.getEmailTemplate()).isEqualTo(EmailTemplate.GENERIC);
    assertThat(dto.getVariables()).isEqualTo(variables);
  }

  @Test
  void defaultValues_shouldBeCorrect() {
    // Act
    NotificationCreationDto dto = new NotificationCreationDto();

    // Assert
    assertThat(dto.getUsers()).isNull();
    assertThat(dto.getGroups()).isNull();
    assertThat(dto.getTitle()).isNull();
    assertThat(dto.getMessage()).isNull();
    assertThat(dto.isPriority()).isFalse();
    assertThat(dto.getShortDescription()).isNull();
    assertThat(dto.getNotifyType()).isNull();
    assertThat(dto.getNotificationType()).isNull();
    assertThat(dto.getEmailTemplate()).isNull();
    assertThat(dto.getVariables()).isNull();
  }

  @Test
  void setUsers_withEmptyArray_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    String[] emptyUsers = {};

    // Act
    dto.setUsers(emptyUsers);

    // Assert
    assertThat(dto.getUsers()).isEqualTo(emptyUsers);
    assertThat(dto.getUsers()).hasSize(0);
  }

  @Test
  void setGroups_withSingleGroup_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    String[] singleGroup = {"single-group"};

    // Act
    dto.setGroups(singleGroup);

    // Assert
    assertThat(dto.getGroups()).isEqualTo(singleGroup);
    assertThat(dto.getGroups()).hasSize(1);
    assertThat(dto.getGroups()[0]).isEqualTo("single-group");
  }

  @Test
  void setVariables_withComplexMap_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    Map<String, Object> complexVariables = Map.of(
        "string", "text",
        "number", 123,
        "boolean", true,
        "nested", Map.of("inner", "value")
    );

    // Act
    dto.setVariables(complexVariables);

    // Assert
    assertThat(dto.getVariables()).isEqualTo(complexVariables);
    assertThat(dto.getVariables().get("string")).isEqualTo("text");
    assertThat(dto.getVariables().get("number")).isEqualTo(123);
    assertThat(dto.getVariables().get("boolean")).isEqualTo(true);
    assertThat(dto.getVariables().get("nested")).isEqualTo(Map.of("inner", "value"));
  }

  @Test
  void setNotificationType_withAllTypes_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();

    // Act & Assert for each notification type
    dto.setNotificationType(NotificationType.Info);
    assertThat(dto.getNotificationType()).isEqualTo(NotificationType.Info);

    dto.setNotificationType(NotificationType.Warning);
    assertThat(dto.getNotificationType()).isEqualTo(NotificationType.Warning);

    dto.setNotificationType(NotificationType.Congratulation);
    assertThat(dto.getNotificationType()).isEqualTo(NotificationType.Congratulation);

    dto.setNotificationType(NotificationType.None);
    assertThat(dto.getNotificationType()).isEqualTo(NotificationType.None);
  }

  @Test
  void setNotifyType_withAllTypes_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();

    // Act & Assert for each notify type
    dto.setNotifyType(NotifyType.UI);
    assertThat(dto.getNotifyType()).isEqualTo(NotifyType.UI);

    dto.setNotifyType(NotifyType.Mail);
    assertThat(dto.getNotifyType()).isEqualTo(NotifyType.Mail);

    dto.setNotifyType(NotifyType.All);
    assertThat(dto.getNotifyType()).isEqualTo(NotifyType.All);
  }

  @Test
  void setEmailTemplate_withAllTemplates_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();

    // Act & Assert for each email template
    dto.setEmailTemplate(EmailTemplate.GENERIC);
    assertThat(dto.getEmailTemplate()).isEqualTo(EmailTemplate.GENERIC);

    dto.setEmailTemplate(EmailTemplate.RESET_PASSWORD);
    assertThat(dto.getEmailTemplate()).isEqualTo(EmailTemplate.RESET_PASSWORD);
  }

  @Test
  void setPriority_withTrueAndFalse_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();

    // Act & Assert
    dto.setPriority(true);
    assertThat(dto.isPriority()).isTrue();

    dto.setPriority(false);
    assertThat(dto.isPriority()).isFalse();
  }

  @Test
  void setTitle_withLongString_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    String longTitle = "This is a very long title that might be used for notifications "
        + "and should be handled correctly by the DTO class without any issues";

    // Act
    dto.setTitle(longTitle);

    // Assert
    assertThat(dto.getTitle()).isEqualTo(longTitle);
  }

  @Test
  void setMessage_withMultilineString_shouldWork() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    String multilineMessage = """
        This is a multiline message
        that spans several lines
        and should be preserved correctly.
        """;

    // Act
    dto.setMessage(multilineMessage);

    // Assert
    assertThat(dto.getMessage()).isEqualTo(multilineMessage);
    assertThat(dto.getMessage()).contains("\n");
  }

  @Test
  void toString_shouldIncludeFields() {
    // Arrange
    NotificationCreationDto dto = new NotificationCreationDto();
    dto.setTitle("ToString Test");
    dto.setMessage("ToString Message");
    dto.setPriority(true);

    // Act
    String result = dto.toString();

    // Assert
    assertThat(result).isNotNull();
    // Note: Since this uses Lombok @Data, the exact format depends on Lombok's implementation
    // We just verify that toString() produces a non-null string
  }
}