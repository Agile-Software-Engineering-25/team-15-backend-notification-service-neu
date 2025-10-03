package com.ase.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * Configuration class for user service endpoints and settings.
 */
@ConfigurationProperties(prefix = "user-service")
@Data
public class UserServiceConfig {

  /**
   * Base URL of the user service.
   */
  private String url;

  /**
   * Whether group notifications are enabled.
   */
  private boolean groupsEnabled;
}
