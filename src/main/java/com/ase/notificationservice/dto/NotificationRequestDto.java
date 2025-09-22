package com.ase.notificationservice.dto;

import lombok.Data;

enum NotificationTypes {
  Mail, UI, All
}
@Data
public class NotificationRequestDto {
  private String[] users;
  private String[] groups;
  private String title;
  private String message;
  private boolean priority;
  private String shortDescription;
  private NotificationTypes notificationType;
}
