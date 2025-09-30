package com.ase.notificationservice.enums;

public enum EmailTemplate {
  WELCOME("welcome"),
  RESET_PASSWORD("reset-password"),
  GENERIC_WITH_CTA("generic-with-cta");

  private final String fileName;
  EmailTemplate(String fileName) { this.fileName = fileName; }
  public String fileName() { return fileName; }
}
