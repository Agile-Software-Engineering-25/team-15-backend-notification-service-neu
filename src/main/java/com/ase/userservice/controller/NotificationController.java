package com.ase.userservice.controller;

import com.ase.userservice.model.Notification;
import com.ase.userservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/{id}/mark-as-unread")
    public ResponseEntity<?> markAsUnread(@PathVariable Long id) {
        boolean success = notificationService.markAsUnread(id);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/mark-as-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        boolean success = notificationService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAndMarkAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return ResponseEntity.status(401).body("Authorization header required (Mock-Auth)");
        }
        Optional<Notification> notification = notificationService.getAndMarkAsRead(id);
        return notification
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
