package com.ase.notificationservice.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class SecurityConfig {

  @Value("${server.servlet.context-path}")
  private  String basePath;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    String[] swaggerPaths = {
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs",
        "/v3/api-docs.yaml",
        "/v3/api-docs/**"
    };

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
        .requestMatchers(swaggerPaths).permitAll()
        .anyRequest().permitAll()
    );
    return http.build();
  }

  /**
   * Placeholder filter for token validation.
   */
  static class PlaceholderTokenFilter extends OncePerRequestFilter {
    private final String basePath;

    PlaceholderTokenFilter(String basePath) {
      this.basePath = basePath;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

      String path = request.getServletPath();
      log.info(path);

      // Allow Swagger/OpenAPI paths without authentication
      if (path.startsWith("/swagger-ui") || path.startsWith("/v1/api-docs") || path.equals("/")) {
        filterChain.doFilter(request, response);
        return;
      }

      String token = request.getHeader("Authorization");
      if (token == null || token.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
            "Missing or empty Authorization header (token required)"
        );
        return;
      }

      filterChain.doFilter(request, response);
    }
  }
}
