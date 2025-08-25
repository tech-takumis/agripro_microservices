package com.hashjosh.userservicev2.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record UserResponseDto(
        String firstName,
        String lastName,
        String email,
        String roles,
        String gender,
        String contactNumber,
        String civilStatus,
        String address,
        String profileType,
        JsonNode roleDetails
) {
}
