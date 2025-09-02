package com.ase.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Creates a custom OpenAPI configuration with security settings.
   *
   * @return configured OpenAPI instance
   */
  @Bean
  public OpenAPI customOpenAPI() {
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY)
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

    return new OpenAPI()
        .info(new Info()
            .title("Notification Service API")
            .version("1.0"))
        .addSecurityItem(new SecurityRequirement()
            .addList("TokenAuth"))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes("TokenAuth", securityScheme));
  }
}
