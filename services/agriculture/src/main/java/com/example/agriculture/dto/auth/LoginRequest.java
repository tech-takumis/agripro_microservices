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
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
            message = "Password must be at least 8 characters long, include an uppercase letter, lowercase letter, number, and special character"
    )
    private String password;
    private boolean rememberMe;
}
