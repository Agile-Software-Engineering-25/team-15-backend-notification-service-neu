package com.ase.notificationservice.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

public class QueryParameterBearerTokenResolver implements BearerTokenResolver {

  public static final String TOKEN_PARAM = "auth_token";

  @Override
  public String resolve(HttpServletRequest request) {
    String token = resolveFromAuthorizationHeader(request);
    if (StringUtils.hasText(token)) {
      return token;
    }

    token = request.getParameter(TOKEN_PARAM);
    if (StringUtils.hasText(token)) {
      return token;
    }

    return null;
  }

  private String resolveFromAuthorizationHeader(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}
