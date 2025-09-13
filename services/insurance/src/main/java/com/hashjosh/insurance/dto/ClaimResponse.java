package com.hashjosh.insurance.dto;

import com.hashjosh.insurance.enums.ClaimStatus;

import java.util.UUID;

public record ClaimResponse(
        UUID claimId,
        UUID applicationId,
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
