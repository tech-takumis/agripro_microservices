package com.hashjosh.kafkacommon.verification;
import com.hashjosh.constant.ApplicationStatus;

import java.util.UUID;

public record VerificationDto(
        UUID verificationId,
        UUID applicationId,
        UUID userId,
        ApplicationStatus status,
        String inspectionType,
        String rejectionReason,
        String report,
        Long version
) {
    // Status is now imported from the shared enum in the verification service
}
