package com.ase.notificationService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.repository")
public class RepositoryConfig {
  private boolean initializeWithDummyData;
}
