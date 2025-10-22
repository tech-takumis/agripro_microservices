package com.hashjosh.identity.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {
    private String tenantKey;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private Map<String, Object> profile;
}