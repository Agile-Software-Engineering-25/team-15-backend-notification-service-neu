package com.ase.userservice.controller;

import com.ase.userservice.model.Notification;
import com.ase.userservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "TokenAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
        summary = "Mark notification as unread",
        description = "Removes the `readAt` timestamp for the given notification. Requires an Authorization header.",
        security = @SecurityRequirement(name = "TokenAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Notification marked as unread",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Notification marked as unread\"")
                })),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Notification not found\"")
                })),
            @ApiResponse(responseCode = "401", description = "Authorization header missing",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Authorization header required (Mock-Auth)\"")
                }))
        }
    )
    @PostMapping("/{id}/mark-as-unread")
    public ResponseEntity<?> markAsUnread(
        @Parameter(description = "Notification ID", example = "1") @PathVariable Long id
    ) {
        boolean success = notificationService.markAsUnread(id);
        if (success) {
            return ResponseEntity.ok("Notification marked as unread");
        } else {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

    @Operation(
        summary = "Mark notification as read",
        description = "Sets the `readAt` timestamp to now for the given notification. Requires an Authorization header.",
        security = @SecurityRequirement(name = "TokenAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Notification marked as read\"")
                })),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Notification not found\"")
                })),
            @ApiResponse(responseCode = "401", description = "Authorization header missing",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Authorization header required (Mock-Auth)\"")
                }))
        }
    )
    @PostMapping("/{id}/mark-as-read")
    public ResponseEntity<?> markAsRead(
        @Parameter(description = "Notification ID", example = "1") @PathVariable Long id
    ) {
        boolean success = notificationService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

    @Operation(
        summary = "Get notification and mark as read",
        description = "Returns all attributes of the notification and marks it as read (sets `readAt` to now). Requires an Authorization header.",
        security = @SecurityRequirement(name = "TokenAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Notification found and marked as read",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        value = "{ \"id\": 1, \"message\": \"Dies ist eine Beispielbenachrichtigung\", \"readAt\": \"2024-06-27T12:34:56.789Z\" }"
                    )
                )),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Notification not found\"")
                })),
            @ApiResponse(responseCode = "401", description = "Authorization header missing",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(value = "\"Authorization header required (Mock-Auth)\"")
                }))
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getAndMarkAsRead(
        @Parameter(description = "Notification ID", example = "1") @PathVariable Long id,
        @Parameter(description = "Authorization token", example = "Bearer mock-token") @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).body("Authorization header required (Mock-Auth)");
        }
        Optional<Notification> notification = notificationService.getAndMarkAsRead(id);
        return notification
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Notification not found"));
    }
}
