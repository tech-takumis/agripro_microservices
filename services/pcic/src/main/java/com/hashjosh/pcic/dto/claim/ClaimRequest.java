package com.hashjosh.pcic.dto.claim;


import com.hashjosh.constant.pcic.enums.ClaimStatus;

public record ClaimRequest(
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
