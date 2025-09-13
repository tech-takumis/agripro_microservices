package com.hashjosh.kafkacommon.verification;

import java.util.UUID;

public record VerificationDto(
        UUID verificationId,
        UUID applicationId,
        UUID userId,
        VerificationStatus status,
        boolean approvedByAdjuster,
        boolean verifiedByUnderwriter,
        String inspectionType,
        String rejectionReason,
        String report,
        Long version
) {

    public enum VerificationStatus{
        APPROVED,
        REJECTED,
    }
}
