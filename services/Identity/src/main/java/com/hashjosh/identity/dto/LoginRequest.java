package com.hashjosh.identity.dto;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String tenantKey;
    private String username;
    private String password;
    private Boolean rememberMe;
}
