package com.ase.notificationservice.dtos;

import com.ase.notificationservice.enums.NotificationTypes;
import lombok.Data;

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
