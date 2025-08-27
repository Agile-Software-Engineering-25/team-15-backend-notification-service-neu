package com.ase.userservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.Instant;

@Entity
@Schema(description = "Notification entity")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Notification ID", example = "1")
    private Long id;

    @Schema(description = "Notification message", example = "Dies ist eine Beispielbenachrichtigung")
    private String message;

    @Schema(description = "Read timestamp", example = "2024-06-27T12:34:56.789Z")
    private Instant readAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
