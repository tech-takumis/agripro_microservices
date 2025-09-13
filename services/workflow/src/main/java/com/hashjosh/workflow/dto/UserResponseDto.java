package com.hashjosh.workflow.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
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
