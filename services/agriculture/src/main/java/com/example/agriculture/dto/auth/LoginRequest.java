package com.example.agriculture.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed up to 100 character")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    private boolean rememberMe;
}
