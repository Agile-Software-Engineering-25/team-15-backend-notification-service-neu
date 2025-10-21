package com.ase.notificationservice.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock")
@RestController
@RequestMapping("/users")
public class MockUserController {
  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable String id) {
    return new UserResponse(id, "nekkrause@gmail.com");
  }

  record UserResponse(String id, String email) {
  }
}
