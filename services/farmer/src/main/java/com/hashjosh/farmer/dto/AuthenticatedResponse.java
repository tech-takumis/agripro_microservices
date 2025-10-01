package com.hashjosh.farmer.dto;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedResponse(
         UUID userId,
         String username,
         String firstName,
         String lastName,
         String email,
         String phoneNumber,
         String address,
         Set<String> roles,
         Set<String> permission
) {
}
