package com.hashjosh.userservicev2.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.userservicev2.models.UserProfile;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        UserProfile.ProfileType profileType,
        JsonNode roleDetails,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
