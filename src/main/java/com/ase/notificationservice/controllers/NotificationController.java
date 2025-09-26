package com.ase.notificationservice.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationservice.dtos.NotificationRequestDto;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.services.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing notification operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping
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
          .userId(user)
          .message(notificationRequestDto.getMessage())
          .title(notificationRequestDto.getTitle())
          .priority(notificationRequestDto.isPriority())
          .shortDescription(notificationRequestDto.getShortDescription())
          .notificationType(String.valueOf(notificationRequestDto.getNotificationType()))
          .receivedAt(receivedTimestamp)
          .build();

      created.add(notificationService.createNotification(notification));
    }

    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<List<Notification>> getNotifications(
      @RequestHeader("X-User-Id") String userId) {
    List<Notification> notifications
        = notificationService.getNotificationsForUser(userId);
    return ResponseEntity.ok(notifications);
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
}
