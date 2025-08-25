package com.hashjosh.userservicev2.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Password is required!")
//        @Pattern(
//                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
//                message = "Password must have at least 8 characters, including uppercase, lowercase, number, and special character"
//        )
        String password,
        Boolean rememberMe
) {
}
