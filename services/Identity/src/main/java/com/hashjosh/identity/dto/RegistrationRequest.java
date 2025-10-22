package com.hashjosh.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegistrationRequest {
    private String tenantKey;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> roles;
    private Object profile;
}

