package com.example.agriculture.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InspectionRecordResponse(
        UUID id,
        UUID applicationId,
        String inspectionType,
        String report,
        LocalDateTime updatedAt,
        Long version
) {
}
