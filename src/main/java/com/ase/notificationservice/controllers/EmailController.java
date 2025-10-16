package com.ase.notificationservice.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import com.ase.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {
  private final EmailService emailService;

  @PostMapping(produces = "application/json")
  public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailNotificationRequestDto req) {
    try {
      emailService.sendEmail(req);
      return ResponseEntity.noContent().build();
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
    catch (MailAuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "SMTP authentication failed"));
    }
    catch (MessagingException | UnsupportedEncodingException e) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
          .body(Map.of("error", "Mail server error"));
    }
  }
}
