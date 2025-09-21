package com.ase.notificationservice.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationservice.dto.NotificationRequestDto;
import com.ase.notificationservice.model.Notification;
import com.ase.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing notification operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "TokenAuth")
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/notification")
  public ResponseEntity<List<Notification>> postNotification(
      @RequestBody NotificationRequestDto notificationRequestDto) {

    List<Notification> created = new ArrayList<>();

    List<String> allUsers = new ArrayList<>();
    if (notificationRequestDto.getUsers() != null) {
      allUsers.addAll(Arrays.asList(notificationRequestDto.getUsers()));
    }

    /* TODO add when API split group is available
    if (notificationRequestDto.getGroups() != null) {
      //ADD API REQUEST FOR GROUP SPLITTING
    }
    */
    Instant receivedTimestamp = Instant.now();

    for (String user : allUsers.stream().distinct().toList()) {
      Notification notification = Notification.builder()
          .user_id(user)
          .message(notificationRequestDto.getMessage())
          .title(notificationRequestDto.getTitle())
          .priority(notificationRequestDto.isPriority())
          .shortDescription(notificationRequestDto.getShortDescription())
          .receivedAt(receivedTimestamp)
          .build();

      created.add(notificationService.createNotification(notification));
    }

    return ResponseEntity.ok(created);
  }

  @PostMapping("/mark-as-unread")
  public ResponseEntity<?> markAsUnread(
      @Parameter(description = "Notification ID", example = "7c50d311-b813-4cdd-b8f8-34e7df684e18")
      @RequestHeader("X-Notification-Id") String id) {
    boolean success = notificationService.markAsUnread(id);
    if (success) {
      return ResponseEntity.ok("Notification marked as unread");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Notification not found");
  }

  @PostMapping("/mark-as-read")
  public ResponseEntity<?> markAsRead(
      @Parameter(description = "Notification ID", example = "1")
      @RequestHeader("X-Notification-Id") String id) {
    boolean success = notificationService.markAsRead(id);
    if (success) {
      return ResponseEntity.ok("Notification marked as read");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Notification not found");
  }

  @GetMapping
  public ResponseEntity<?> getAndMarkAsRead(
      @Parameter(description = "Notification ID", example = "1")
      @RequestHeader("X-Notification-Id") String id,
      @Parameter(description = "Authorization token",
          example = "Bearer mock-token")
      @RequestHeader(value = "Authorization",
          required = false)
      String authorization) {
    if (authorization == null || authorization.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Authorization header required (Mock-Auth)");
    }
    Optional<Notification> notification
        = notificationService.getAndMarkAsRead(id);
    return notification
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Notification not found"));
  }
}
