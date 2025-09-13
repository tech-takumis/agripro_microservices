package com.hashjosh.workflow.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowResponseDto(
        UUID id,
        UUID applicationId,
        String applicationName,
        String status,
        String comments,
        UUID updatedBy,
        String uploaderName,
        LocalDateTime updatedAt,
        Long version
) {
}
