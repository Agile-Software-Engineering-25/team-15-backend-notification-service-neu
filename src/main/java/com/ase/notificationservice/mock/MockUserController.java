package com.ase.notificationservice.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("mock-users")
@RestController
@RequestMapping("/users")
public class MockUserController {
  record UserResponse(String id, String email) {}

  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable String id) {
    return new UserResponse(id, "example@example.com");
  }
}
