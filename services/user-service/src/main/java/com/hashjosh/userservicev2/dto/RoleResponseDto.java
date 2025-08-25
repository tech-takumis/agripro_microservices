package com.hashjosh.userservicev2.dto;

import java.util.Set;

public record RoleResponseDto(
        Long id,
        String name,
        Set<String> authorities
) {
}
