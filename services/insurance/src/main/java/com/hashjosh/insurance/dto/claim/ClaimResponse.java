package com.hashjosh.insurance.dto.claim;


import com.hashjosh.constant.pcic.enums.ClaimStatus;

import java.util.UUID;

public record ClaimResponse(
        UUID claimId,
        UUID applicationId,
        Double claimAmount,
        ClaimStatus payoutStatus
) {
}
