package com.hashjosh.userservicev2.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.userservicev2.models.UserProfile;

public record UserProfileRequest(
        UserProfile.ProfileType profileType,
        JsonNode roleDetails
) {
}
