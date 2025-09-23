package com.ase.notificationservice;

import java.util.List;
import com.ase.notificationservice.enums.NotificationTypes;
import com.ase.notificationservice.model.Notification;


public class DummyData {

  public static final List<Notification> NOTIFICATIONS = List.of(
      Notification.builder()
          .userId("1")
          .message("Test Notification 1")
          .title("Test Notification 1")
          .shortDescription("short description 1")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 2")
          .title("Test Notification 2")
          .shortDescription("short description 2")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 3")
          .title("Test Notification 3")
          .shortDescription("short description 3")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 4")
          .title("Test Notification 4")
          .shortDescription("short description 4")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 5")
          .title("Test Notification 5")
          .shortDescription("short description 5")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 6")
          .title("Test Notification 6")
          .shortDescription("short description 6")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 7")
          .title("Test Notification 7")
          .shortDescription("short description 7")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 8")
          .title("Test Notification 8")
          .shortDescription("short description 8")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 9")
          .title("Test Notification 9")
          .shortDescription("short description 9")
          .notificationType(NotificationTypes.All.toString())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 10")
          .title("Test Notification 10")
          .shortDescription("short description 10")
          .notificationType(NotificationTypes.All.toString())
          .build()
  );
}
