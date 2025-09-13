package com.hashjosh.verification.dto;

public record VerificationRequest(
        String status,
        boolean approvedByAdjuster,
        boolean verifiedByUnderwriter,
        String inspectionType,
        String rejectionReason,
        String report
) {
}
