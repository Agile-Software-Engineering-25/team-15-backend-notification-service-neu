package com.ase.notificationservice;

import java.util.List;
import com.ase.notificationservice.model.Notification;


public class DummyData {

  public static final List<Notification> NOTIFICATIONS = List.of(
      Notification.builder().message("Test Notification 1").build(),
      Notification.builder().message("Test Notification 2").build()
  );
}
