package com.hashjosh.pcic.dto;


import com.hashjosh.pcic.enums.ClaimStatus;

public record ClaimRequest(
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
