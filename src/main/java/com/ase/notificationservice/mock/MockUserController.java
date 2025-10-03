package com.ase.notificationservice.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("mock-users")
@RestController
@RequestMapping("/users")
public class MockUserController {
  record UserResp(String id, String email) {}

  @GetMapping("/{id}")
  public UserResp getUser(@PathVariable String id) {
    return new UserResp(id,"example@example.com");
  }
}
