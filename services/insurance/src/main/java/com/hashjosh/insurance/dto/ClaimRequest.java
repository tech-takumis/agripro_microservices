package com.hashjosh.insurance.dto;

import com.hashjosh.insurance.enums.ClaimStatus;

public record ClaimRequest(
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
