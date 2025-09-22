package com.ase.notificationservice.dto;

import lombok.Data;

@Data
public class NotificationRequestDto {
  private String[] users;
  private String[] groups;
  private String title;
  private String message;
  private boolean priority;
  private String shortDescription;
  private String notificationType;
}
