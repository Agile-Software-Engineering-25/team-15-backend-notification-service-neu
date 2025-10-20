package com.ase.notificationservice.dtos;

import java.util.Map;
import com.ase.notificationservice.enums.EmailTemplate;
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
  private EmailTemplate emailTemplate;
  private Map<String, Object> variables;
}
