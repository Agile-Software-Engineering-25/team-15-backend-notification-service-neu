package com.ase.notificationservice.services;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.AllArgsConstructor;
import com.ase.notificationservice.dtos.EmailNotificationRequestDto;

@Service
@AllArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.fromAddress:noreply@sau-portal.de}")
  private String fromAddress;

  @Value("${spring.mail.fromName:}")
  private String fromName;

  @Async
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500, multiplier = 2.0))
  public void sendEmail(@NonNull EmailNotificationRequestDto req)
      throws MessagingException, UnsupportedEncodingException {
    for (String recipient : req.to()) {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      if (fromName != null && !fromName.isBlank()) {
        helper.setFrom(fromAddress.trim(), fromName);
      }
      else {
        helper.setFrom(fromAddress.trim());
      }

      if (req.replyTo() != null && !req.replyTo().isBlank()) {
        helper.setReplyTo(req.replyTo());
      }

      helper.setTo(recipient);
      helper.setSubject(req.subject());

      String html = resolveHtml(req, recipient);
      String text = resolveText(req, html);

      helper.setText(text, html);
      mailSender.send(message);
    }
  }

  private String resolveHtml(EmailNotificationRequestDto req, String recipientEmail) {
    if (req.template() != null) {
      Context ctx = new Context();
      Map<String, Object> vars = new HashMap<>();
      if (req.variables() != null) {
        vars.putAll(req.variables());
      }
      if (req.ctaLink() != null && !req.ctaLink().isBlank()) {
        vars.put("ctaLink", req.ctaLink());
      }
      if (recipientEmail != null) {
        vars.put("recipientEmail", recipientEmail);
      }
      ctx.setVariables(vars);
      return templateEngine.process(req.template().getFileName(), ctx);
    }

    if (req.text() != null && !req.text().isBlank()) {
      return "<pre>" + escape(req.text()) + "</pre>";
    }
    throw new IllegalArgumentException("no content ,provide either a template or text");
  }


  private String resolveText(EmailNotificationRequestDto req, String html) {
    if (req.text() != null && !req.text().isBlank()) {
      return req.text();
    }
    return html.replaceAll("<[^>]+>", "");
  }

  private static String escape(String s) {
    return s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
  }

  private String injectRecipient(
      String content, String recipientEmail) {
    String email = (recipientEmail == null) ? "" : recipientEmail;
    return content.replace("${recipientEmail}", email);
  }
}
