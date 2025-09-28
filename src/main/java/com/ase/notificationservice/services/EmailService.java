package com.ase.notificationservice.services;

import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${app.mail.defaultFrom:sau.portal.noreply@gmail.com}")
  private String defaultFrom;

  public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  @Async
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500, multiplier = 2.0))
  public void sendEmail(@NonNull EmailNotificationRequestDto req) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setFrom(defaultFrom);
      if (req.replyTo() != null && !req.replyTo().isEmpty()) helper.setReplyTo(req.replyTo());
      helper.setTo(req.to().toArray(String[]::new));
      helper.setSubject(req.subject());

      String html = resolveHTML(req);
      String text = (req.text() != null && !req.text().isBlank()) ? req.text() : html.replaceAll("<[^>]+>", "");
      helper.setText(text, html);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send email",e);
    }
  }
  private String resolveHTML(EmailNotificationRequestDto req) {
    if (req.html() != null && !req.html().isBlank()) return req.html();
    if (req.template() != null && !req.template().isBlank()) {
      var context = new Context();
      if (req.variables() != null) context.setVariables(req.variables());
      return templateEngine.process(req.template(), context);
    }
    if (req.text() != null && !req.text().isBlank()){
      return "<pre>" + escape(req.text()) + "</pre>";
    }
    return "<p>(no content)</p>";
  }
  private static String escape(String s) {
    return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
  }
}
