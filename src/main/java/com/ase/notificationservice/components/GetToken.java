package com.ase.notificationservice.components;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import com.ase.notificationservice.entities.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class GetToken {
  private static final Logger log = LoggerFactory.getLogger(GetToken.class);

  private String token;
  @Value("${spring.security.token.client-id}") String clientId;
  @Value("${spring.security.token.client-secret}") String clientSecret;

  public String makehttpcall() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    String url = "client_id=%s&grant_type=client_credentials&client_secret=%s";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://keycloak.sau-portal.de/realms/sau/protocol/openid-connect/token"))
        .POST(BodyPublishers.ofString(String.format(url, clientId, clientSecret)))
        .setHeader("Content-Type", "application/x-www-form-urlencoded")
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    int status = response.statusCode();
    if (status == 200) {
      log.info("Keycloak token endpoint responded with status=200 (OK)");
    }
    else {
      String body = response.body();
      String safeBody = body == null ? null : (body.length() > 1000 ? body.substring(0, 1000) + "..." : body);
      log.warn("Keycloak token endpoint error: status={}, body={}", status, safeBody);
    }

    return response.body();
  }

  public String parseJson(String body) throws JsonMappingException, JsonProcessingException{
    ObjectMapper mapper = new ObjectMapper();
    TokenResponse jsontoken = mapper.readValue(body,TokenResponse.class);
    token = jsontoken.access_token;
    return token;
  }

  public String getToken() throws JsonMappingException, JsonProcessingException, IOException, InterruptedException {
    return parseJson(makehttpcall());
  }

}
