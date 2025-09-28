package com.ase.notificationservice.dtos;

import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;
import lombok.Data;

@Data
public class NotificationCreationDto {
  private String[] users;
  private String[] groups;
  private String title;
  private String message;
  private boolean priority;
  private String shortDescription;
  private NotifyType notifyType;
  private NotificationType notificationType;
}
