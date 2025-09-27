package com.ase.notificationservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDto {
  @JsonProperty("user_id")
  private String id;
}
