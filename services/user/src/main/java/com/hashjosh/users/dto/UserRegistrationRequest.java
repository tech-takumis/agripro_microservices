package com.hashjosh.users.dto;

import com.hashjosh.kafkacommon.user.TenantType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class UserRegistrationRequest {
    private TenantType tenantId;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<UUID> rolesId;
}

