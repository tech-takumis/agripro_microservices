package com.hashjosh.pcic.mapper;

import com.hashjosh.pcic.dto.claim.ClaimResponse;
import com.hashjosh.pcic.entity.Claim;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {
    public ClaimResponse toClaimResponse(Claim savedClaim) {
        return new ClaimResponse(
            savedClaim.getId(),
                savedClaim.getSubmissionId(),
                savedClaim.getClaimAmount(),
                savedClaim.getPayoutStatus()
        );
    }
}
