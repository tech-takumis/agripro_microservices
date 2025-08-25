package com.hashjosh.userservicev2.dto;

public record FarmerRequestDto(
        String referenceNumber,
        UserProfileRequest userProfile
) {
}
