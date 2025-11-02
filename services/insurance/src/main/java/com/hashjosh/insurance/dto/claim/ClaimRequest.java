package com.hashjosh.insurance.dto.claim;


import com.hashjosh.constant.pcic.enums.ClaimStatus;

import java.util.UUID;

public record ClaimRequest(

        UUID submissionId,
        UUID policyId,
        Double claimAmount,
        ClaimStatus payoutStatus,
        String rejectionReason
) {
}
