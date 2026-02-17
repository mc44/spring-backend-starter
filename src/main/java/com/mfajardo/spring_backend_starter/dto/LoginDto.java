package com.mfajardo.spring_backend_starter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "Username must not be empty") String username;
    @NotBlank(message = "Password must not be empty") String password;
}
