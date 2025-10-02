package com.ase.notificationservice.dtos;

import java.util.List;
import java.util.Map;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import com.ase.notificationservice.enums.EmailTemplate;

public record EmailNotificationRequestDto(
    @NotEmpty List<@Email String> to,
    @NotBlank String subject,
    String text,
    EmailTemplate template,
    Map<String, Object> variables,
    String ctaLink,
    String replyTo
) {

}
