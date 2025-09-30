package com.ase.notificationservice.dtos;

import com.ase.notificationservice.enums.EmailTemplate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record EmailNotificationRequestDto(
    @NotEmpty List<@Email String> to,
    @NotBlank String subject,
    String text,                 // plain text (optional)
    String html,// raw HTML (optional)
    EmailTemplate template,             // template name under templates/email (optional)
    Map<String, Object> variables,  // template variables
    String replyTo, // optional
    String ctaLink  // optional
) {

}
