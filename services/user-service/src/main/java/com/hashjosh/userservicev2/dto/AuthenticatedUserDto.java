package com.hashjosh.userservicev2.dto;

import com.hashjosh.userservicev2.models.Role;

public record AuthenticatedUserDto(
        String username,
        String firstName,
        String lastName,
        String email,
        String role
) {
}
