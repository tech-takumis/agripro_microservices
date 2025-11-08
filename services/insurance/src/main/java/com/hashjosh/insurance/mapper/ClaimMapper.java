package com.hashjosh.insurance.mapper;

import com.hashjosh.constant.pcic.enums.ClaimStatus;
import com.hashjosh.insurance.dto.claim.ClaimRequest;
import com.hashjosh.insurance.dto.claim.ClaimResponse;
import com.hashjosh.insurance.entity.Claim;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {
    public ClaimResponse toClaimResponse(Claim savedClaim) {
        return new ClaimResponse(
            savedClaim.getId(),
                savedClaim.getClaimAmount(),
                savedClaim.getPayoutStatus()
        );
    }

    public Claim toClaimEntity(ClaimRequest claim) {
        return Claim.builder()
                .claimAmount(claim.claimAmount())
                .payoutStatus(ClaimStatus.PENDING)
                .policyId(claim.policyId())
                .build();
    }

    public Claim updateClaimEntity(Claim existingClaim, ClaimRequest claimRequest) {
        existingClaim.setClaimAmount(claimRequest.claimAmount());
        existingClaim.setPayoutStatus(claimRequest.payoutStatus());
        return existingClaim;
    }
}
