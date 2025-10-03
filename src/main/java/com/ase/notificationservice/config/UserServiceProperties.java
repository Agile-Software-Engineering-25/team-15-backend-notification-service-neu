package com.ase.notificationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "userservice")
@Setter
@Getter
public class UserServiceProperties {
  private String url="http://localhost:8081";
  private int timeoutMs = 2000;

}
