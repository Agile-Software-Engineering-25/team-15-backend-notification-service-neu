package com.ase.notificationservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.ase.notificationservice.config.security.CorsConfigProperties;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(CorsConfigProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final CorsConfigProperties corsConfigProperties;

  public WebSocketConfig(CorsConfigProperties corsConfigProperties) {
    this.corsConfigProperties = corsConfigProperties;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    String[] allowedOrigins = corsConfigProperties.allowedOrigins();

    registry.addEndpoint("/websocket")   //endpoint
        .setAllowedOriginPatterns(allowedOrigins)
        .withSockJS();  // SockJS support
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }
}
