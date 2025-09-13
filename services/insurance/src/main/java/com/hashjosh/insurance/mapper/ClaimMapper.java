package com.hashjosh.insurance.mapper;

import com.hashjosh.insurance.dto.ClaimResponse;
import com.hashjosh.insurance.model.Claim;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {
    public ClaimResponse toClaimResponse(Claim savedClaim) {
        return new ClaimResponse(
            savedClaim.getId(),
                savedClaim.getApplicationId(),
                savedClaim.getClaimAmount(),
                savedClaim.getPayoutStatus(),
                savedClaim.getRejectionReason()
        );
    }
}
