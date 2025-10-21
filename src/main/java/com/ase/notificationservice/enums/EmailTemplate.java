package com.ase.notificationservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailTemplate {
  RESET_PASSWORD("password"),
  GENERIC("generic-template");
  
  private final String fileName;

}
