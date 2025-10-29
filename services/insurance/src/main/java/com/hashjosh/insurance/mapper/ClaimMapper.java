package com.hashjosh.insurance.mapper;

import com.hashjosh.insurance.dto.claim.ClaimResponse;
import com.hashjosh.insurance.entity.Claim;
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
