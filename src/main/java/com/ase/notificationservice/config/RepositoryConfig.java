package com.ase.notificationservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.repository")
public class RepositoryConfig {
  private boolean initializeWithDummyData;
}
