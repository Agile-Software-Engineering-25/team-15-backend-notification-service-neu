package com.ase.notificationservice.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Unit tests for GetToken component.
 */
@ExtendWith(MockitoExtension.class)
class GetTokenSimpleTest {

  @InjectMocks
  private GetToken getToken;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(getToken, "clientId", "test-client-id");
    ReflectionTestUtils.setField(getToken, "clientSecret", "test-client-secret");
  }

  @Test
  void parseJson_withValidJson_shouldReturnAccessToken() throws JsonProcessingException {
    // Arrange
    String jsonResponse = """
        {
          "access_token": "test-access-token-12345",
          "token_type": "Bearer",
          "expires_in": 3600
        }
        """;

    // Act
    String result = getToken.parseJson(jsonResponse);

    // Assert
    assertThat(result).isEqualTo("test-access-token-12345");
  }

  @Test
  void parseJson_withInvalidJson_shouldThrowJsonParseException() {
    // Arrange
    String invalidJson = "{ invalid json }";

    // Act & Assert
    assertThrows(JsonParseException.class, () -> getToken.parseJson(invalidJson));
  }

  @Test
  void parseJson_withEmptyJson_shouldThrowJsonProcessingException() {
    // Arrange
    String emptyJson = "";

    // Act & Assert
    assertThrows(JsonProcessingException.class, () -> getToken.parseJson(emptyJson));
  }

  @Test
  void parseJson_withMissingAccessToken_shouldReturnNull() throws JsonProcessingException {
    // Arrange
    String jsonWithoutAccessToken = """
        {
          "token_type": "Bearer",
          "expires_in": 3600
        }
        """;

    // Act
    String result = getToken.parseJson(jsonWithoutAccessToken);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  void parseJson_withNullAccessToken_shouldReturnNull() throws JsonProcessingException {
    // Arrange
    String jsonWithNullAccessToken = """
        {
          "access_token": null,
          "token_type": "Bearer",
          "expires_in": 3600
        }
        """;

    // Act
    String result = getToken.parseJson(jsonWithNullAccessToken);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  void parseJson_withComplexValidJson_shouldReturnAccessToken() throws JsonProcessingException {
    // Arrange
    String complexJson = """
        {
          "access_token": "complex-token-xyz-789",
          "token_type": "Bearer",
          "expires_in": 7200,
          "refresh_token": "refresh-token-abc",
          "scope": "read write",
          "custom_field": {
            "nested": "value"
          }
        }
        """;

    // Act
    String result = getToken.parseJson(complexJson);

    // Assert
    assertThat(result).isEqualTo("complex-token-xyz-789");
  }

  @Test
  void parseJson_withEmptyAccessToken_shouldReturnEmptyString() throws JsonProcessingException {
    // Arrange
    String jsonWithEmptyAccessToken = """
        {
          "access_token": "",
          "token_type": "Bearer",
          "expires_in": 3600
        }
        """;

    // Act
    String result = getToken.parseJson(jsonWithEmptyAccessToken);

    // Assert
    assertThat(result).isEqualTo("");
  }

  @Test
  void parseJson_withMalformedJson_shouldThrowJsonProcessingException() {
    // Arrange
    String malformedJson = """
        {
          "access_token": "test-token"
          "missing_comma": true
        }
        """;

    // Act & Assert
    assertThrows(JsonProcessingException.class, () -> getToken.parseJson(malformedJson));
  }

  @Test
  void parseJson_withNestedAccessToken_shouldReturnFirstLevelToken() throws JsonProcessingException {
    // Arrange
    String nestedJson = """
        {
          "access_token": "top-level-token",
          "nested": {
            "access_token": "nested-token"
          }
        }
        """;

    // Act
    String result = getToken.parseJson(nestedJson);

    // Assert
    assertThat(result).isEqualTo("top-level-token");
  }

  @Test
  void parseJson_withSpecialCharactersInToken_shouldReturnToken() throws JsonProcessingException {
    // Arrange
    String jsonWithSpecialChars = """
        {
          "access_token": "token-with-special-chars_plus_equals_etc",
          "token_type": "Bearer"
        }
        """;

    // Act
    String result = getToken.parseJson(jsonWithSpecialChars);

    // Assert
    assertThat(result).isEqualTo("token-with-special-chars_plus_equals_etc");
  }
}