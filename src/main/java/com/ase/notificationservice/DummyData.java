package com.ase.notificationservice;

import java.util.List;
import com.ase.notificationservice.entities.Notification;
import com.ase.notificationservice.enums.NotificationType;
import com.ase.notificationservice.enums.NotifyType;

public class DummyData {

  public static final List<Notification> NOTIFICATIONS = List.of(
      Notification.builder()
          .userId("1")
          .message("Test Notification 1")
          .title("Test Notification 1")
          .shortDescription("This is a short description 1")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 2")
          .title("Test Notification 2")
          .shortDescription("This is a short description 2")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Congratulation)
          .receivedAt(java.time.Instant.now().minus(java.time.Duration.ofMinutes(1)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 3")
          .title("Test Notification 3")
          .shortDescription("This is a short description 3")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Warning)
          .receivedAt(java.time.Instant.now().minus(java.time.Duration.ofMinutes(10)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 4")
          .title("Test Notification 4")
          .shortDescription("This is a short description of my  slightly longer notification that does not fit on the screen 4")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.None)
          .receivedAt(java.time.Instant.now().minus( java.time.Duration.ofMinutes(60)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 5")
          .title("Test Notification 5")
          .shortDescription("This is a short description of my  slightly longer notification that does not fit on the screen 5")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now().minus( java.time.Duration.ofMinutes(120)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 6")
          .title("Test Notification 6")
          .shortDescription("This is a short description of my  slightly longer notification that does not fit on the screen 6")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now().minus( java.time.Duration.ofDays(1)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 7")
          .title("Test Notification 7")
          .shortDescription("This is a short description 7")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now().minus( java.time.Duration.ofDays(2)))
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 8")
          .title("Test Notification 8")
          .shortDescription("This is a short description 8")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 9")
          .title("Test Notification 9")
          .shortDescription("This is a short description 9")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now())
          .build(),
      Notification.builder()
          .userId("1")
          .message("Test Notification 10")
          .title("Test Notification 10")
          .shortDescription("This is a short description 10")
          .notifyType(NotifyType.All)
          .notificationType(NotificationType.Info)
          .receivedAt(java.time.Instant.now())
          .build()
  );
}
