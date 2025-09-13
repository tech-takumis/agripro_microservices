package com.hashjosh.users.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;
}
