package com.hashjosh.verification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationResponse(
        UUID id,
        UUID applicationId,
        String status,
        String inspectionType,
        String rejectionReason,
        String report,
        LocalDateTime verifiedAt,
        LocalDateTime updatedAt,
        Long version
) {
}
