package com.ase.notificationservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsConfigProperties(String[] allowedOrigins) {}
