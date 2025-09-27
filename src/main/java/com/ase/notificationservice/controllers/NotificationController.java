package com.ase.notificationservice.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.ase.notificationservice.dtos.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationservice.dtos.NotificationCreationDto;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.services.NotificationService;
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
      @RequestBody NotificationCreationDto notificationCreationDto) {

    List<Notification> created = new ArrayList<>();

    List<String> allUsers = new ArrayList<>();
    if (notificationCreationDto.getUsers() != null) {
      allUsers.addAll(Arrays.asList(notificationCreationDto.getUsers()));
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
          .message(notificationCreationDto.getMessage())
          .title(notificationCreationDto.getTitle())
          .priority(notificationCreationDto.isPriority())
          .shortDescription(notificationCreationDto.getShortDescription())
          .notificationType(String.valueOf(notificationCreationDto.getNotificationType()))
          .receivedAt(receivedTimestamp)
          .build();

      created.add(notificationService.createNotification(notification));
    }

    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<List<Notification>> getNotifications(
      @RequestBody UserDto user) {
    List<Notification> notifications
        = notificationService.getNotificationsForUser(user.getId());
    return ResponseEntity.ok(notifications);
  }

  @PostMapping("/mark-as-unread/{notificationId}")
  public ResponseEntity<?> markAsUnread(
      @PathVariable String notificationId) {
    boolean success = notificationService.markAsUnread(notificationId);
    if (success) {
      return ResponseEntity.ok("Notification marked as unread");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Notification not found");
  }

  @PostMapping("/mark-as-read/{notificationId}")
  public ResponseEntity<?> markAsRead(
      @PathVariable String notificationId) {
    boolean success = notificationService.markAsRead(notificationId);
    if (success) {
      return ResponseEntity.ok("Notification marked as read");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Notification not found");
  }
}
