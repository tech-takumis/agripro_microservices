package com.hashjosh.application.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponseDto(
        UUID id,
        UUID applicationTypeId,
        UUID userId,
        JsonNode dynamicFields,
        LocalDateTime  submittedAt,
        LocalDateTime updatedAt,
        Long version
) {
}
