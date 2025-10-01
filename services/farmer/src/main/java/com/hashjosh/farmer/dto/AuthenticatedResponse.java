package com.hashjosh.farmer.dto;

import com.hashjosh.farmer.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthenticatedResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
    private Set<String> permissions;
    private UserProfile profile;
}
