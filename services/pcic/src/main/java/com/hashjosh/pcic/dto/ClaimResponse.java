package com.hashjosh.pcic.dto;


import com.hashjosh.pcic.enums.ClaimStatus;

import java.util.UUID;

public record ClaimResponse(
        UUID claimId,
        UUID applicationId,
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
