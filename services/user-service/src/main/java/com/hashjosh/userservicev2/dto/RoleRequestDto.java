package com.hashjosh.userservicev2.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RoleRequestDto(
        @NotBlank(message = "Role name is required!")
        String name,
        Set<Long> permissionIds
 ) {
}
