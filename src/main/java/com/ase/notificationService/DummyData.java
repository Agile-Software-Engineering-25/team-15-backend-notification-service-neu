package com.ase.notificationService;

import com.ase.notificationService.model.Notification;
import java.util.List;

public class DummyData {

  public static final List<Notification> NOTIFICATIONS = List.of(
    Notification.builder().message("Test Notification 1").build(),
    Notification.builder().message("Test Notification 2").build()
  );
}
