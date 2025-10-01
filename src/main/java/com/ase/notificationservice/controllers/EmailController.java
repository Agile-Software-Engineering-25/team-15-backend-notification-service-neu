package com.ase.notificationservice.controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import com.ase.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;

  @PostMapping("/email")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void sendEmail(@Valid @RequestBody EmailNotificationRequestDto req)
      throws MessagingException, UnsupportedEncodingException {
    emailService.sendEmail(req);
  }
}
