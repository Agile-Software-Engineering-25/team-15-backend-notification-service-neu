package com.ase.notificationservice.services;

import com.ase.notificationservice.dtos.EmailNotificationRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.fromAddress:noreply@sau-portal.de}")
  private String fromAddress;

  @Value("${spring.mail.fromName:}")
  private String fromName;

  public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  @Async
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500, multiplier = 2.0))
  public void sendEmail(@NonNull EmailNotificationRequestDto req) {
    String baseHtml = resolveHtml(req, null);
    String baseText = resolveText(req, baseHtml);

    for (String recipient : req.to()) {
      try {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        // From (safe + optional display name)
        InternetAddress from = new InternetAddress(fromAddress.trim());
        if (fromName != null && !fromName.isBlank()) from.setPersonal(fromName);
        helper.setFrom(from);

        if (req.replyTo() != null && !req.replyTo().isBlank()) helper.setReplyTo(req.replyTo());
        helper.setTo(recipient);
        helper.setSubject(req.subject());

        // If you want recipient-specific variables, render per recipient:
        String html = injectRecipient(baseHtml, recipient, req);
        String text = injectRecipient(baseText, recipient, req);

        helper.setText(text, html);
        mailSender.send(message);
      } catch (MessagingException e) {
        throw new RuntimeException("Failed to send email", e);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
  }
  private String resolveHtml(EmailNotificationRequestDto req, String recipientEmail) {
    if (req.html() != null && !req.html().isBlank()) return req.html();

    // Template path via enum
    if (req.template() != null) {
      var ctx = new Context();
      Map<String, Object> vars = new HashMap<>();
      if (req.variables() != null) vars.putAll(req.variables());
      if (req.ctaLink() != null && !req.ctaLink().isBlank()) vars.put("ctaLink", req.ctaLink());
      if (recipientEmail != null) vars.put("recipientEmail", recipientEmail);
      ctx.setVariables(vars);
      return templateEngine.process(req.template().fileName(), ctx);
    }

    // Fallback wrap text
    if (req.text() != null && !req.text().isBlank()) {
      return "<pre>" + escape(req.text()) + "</pre>";
    }
    return "<p>(no content)</p>";
  }
  private String resolveText(EmailNotificationRequestDto req, String html) {
    if (req.text() != null && !req.text().isBlank()) return req.text();
    return html.replaceAll("<[^>]+>", "");
  }

  private static String escape(String s) {
    return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
  }

  private String injectRecipient(String content, String recipientEmail, EmailNotificationRequestDto req) {
    // Trivial replacement; if you need full per-recipient rendering, call resolveHtml(req, recipientEmail)
    return content
        .replace("${recipientEmail}", recipientEmail == null ? "" : recipientEmail);
  }
}
