package com.hashjosh.verification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationResponse(
        UUID id,
        UUID applicationId,
        String status,
        boolean approvedByAdjuster,
        boolean verifiedByUnderwriter,
        String inspectionType,
        String rejectionReason,
        String report,
        LocalDateTime verifiedAt,
        LocalDateTime updatedAt,
        Long version
) {
}
