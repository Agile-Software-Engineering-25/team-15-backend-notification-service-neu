package com.ase.notificationService.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationService.model.Notification;
import com.ase.notificationService.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * REST controller for managing notification operations.
 */
@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "TokenAuth")
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * Creates a new NotificationController with the given service.
   *
   * @param notificationService the notification service to use
   */
  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Operation(
      summary = "Mark notification as unread",
      description
      = "Removes the `readAt` timestamp for the given notification. "
      + "Requires an Authorization header.",
      security = @SecurityRequirement(name = "TokenAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Notification marked as unread",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Notification marked as unread\""))),
          @ApiResponse(
              responseCode = "404",
              description = "Notification not found",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Notification not found\""))),
          @ApiResponse(
              responseCode = "401",
              description = "Authorization header missing",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Authorization header required (Mock-Auth)\"")))
      })
  @PostMapping("/mark-as-unread")
  public ResponseEntity<?> markAsUnread(
      @Parameter(description = "Notification ID", example = "1")
      @RequestHeader("X-Notification-Id") Long id) {
    boolean success = notificationService.markAsUnread(id);
    if (success) {
      return ResponseEntity.ok("Notification marked as unread");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
    .body("Notification not found");
  }

  @Operation(
      summary = "Mark notification as read",
      description
      = "Sets the `readAt` timestamp to now for the given notification. "
      + "Requires an Authorization header.",
      security = @SecurityRequirement(name = "TokenAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Notification marked as read",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Notification marked as read\""))),
          @ApiResponse(
              responseCode = "404",
              description = "Notification not found",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Notification not found\""))),
          @ApiResponse(
              responseCode = "401",
              description = "Authorization header missing",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Authorization header required (Mock-Auth)\"")))
      })
  @PostMapping("/mark-as-read")
  public ResponseEntity<?> markAsRead(
      @Parameter(description = "Notification ID", example = "1")
      @RequestHeader("X-Notification-Id") Long id) {
    boolean success = notificationService.markAsRead(id);
    if (success) {
      return ResponseEntity.ok("Notification marked as read");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
    .body("Notification not found");
  }

  @Operation(
      summary = "Get notification and mark as read",
      description
      = "Returns all attributes of the notification, and marks it as read "
      + "(sets `readAt` to now). Requires an Authorization header.",
      security = @SecurityRequirement(name = "TokenAuth"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Notification found and marked as read",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(value = """
                      {
                        "id": 1,
                        "message": "Dies ist eine Beispielbenachrichtigung",
                        "readAt": "2024-06-27T12:34:56.789Z"
                      }"""))),
          @ApiResponse(
              responseCode = "404",
              description = "Notification not found",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Notification not found\""))),
          @ApiResponse(
              responseCode = "401",
              description = "Authorization header missing",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(
                      value = "\"Authorization header required (Mock-Auth)\"")))
      })
  @GetMapping
  public ResponseEntity<?> getAndMarkAsRead(
      @Parameter(description = "Notification ID", example = "1")
      @RequestHeader("X-Notification-Id") Long id,
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
