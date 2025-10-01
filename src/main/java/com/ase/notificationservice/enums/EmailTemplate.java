package com.ase.notificationservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailTemplate {
  WELCOME("welcome"),
  RESET_PASSWORD("reset-password"),
  GENERIC_WITH_CTA("generic-with-cta");

  private final String fileName;

}
