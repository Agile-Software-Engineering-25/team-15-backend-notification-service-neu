package com.ase.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for user service endpoints and settings.
 */
@Configuration
@ConfigurationProperties(prefix = "userservice")
@Getter
@Setter
public class UserServiceConfig {

  /**
   * Base URL of the user service.
   */
  private String url = "http://localhost:8081";

  /**
   * Whether group notifications are enabled.
   */
  private boolean groupsEnabled = false;
}
