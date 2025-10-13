package com.example.agriculture.dto.auth;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgricultureResponseDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
}
