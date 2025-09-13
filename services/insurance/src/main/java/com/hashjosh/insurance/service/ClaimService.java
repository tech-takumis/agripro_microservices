package com.hashjosh.insurance.service;


import com.hashjosh.insurance.dto.ClaimRequest;
import com.hashjosh.insurance.dto.ClaimResponse;
import com.hashjosh.insurance.enums.ClaimStatus;
import com.hashjosh.insurance.enums.PolicyStatus;
import com.hashjosh.insurance.exceptions.ClaimNotFoundException;
import com.hashjosh.insurance.mapper.ClaimMapper;
import com.hashjosh.insurance.model.Claim;
import com.hashjosh.insurance.model.Policy;
import com.hashjosh.insurance.repository.ClaimRepository;
import com.hashjosh.insurance.repository.PolicyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final ClaimMapper claimMapper;

    public ClaimResponse updateClaim(UUID claimId,
                                     ClaimRequest claim,
                                     HttpServletRequest request) {
        Claim savedClaim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(
                        "Claim id "+ claimId + " not found!",
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI()
                ));

        savedClaim.setClaimAmount(claim.claimAmount());
        savedClaim.setPayoutStatus(claim.payoutStatus());

        if(claim.payoutStatus() != ClaimStatus.REJECTED &&
                claim.payoutStatus() != ClaimStatus.PENDING) {

            savedClaim.setRejectionReason(claim.rejectionReason());

            Policy policy = new Policy();

            policy.setApplicationId(savedClaim.getApplicationId());
            policy.setPolicyNumber(UUID.randomUUID().toString());
            policy.setStatus(PolicyStatus.ACTIVE);
            policy.setCoverageAmount(savedClaim.getClaimAmount());

            Policy savedPolicy = policyRepository.save(policy);

            log.info("Policy created successfully for claim {} and policy number {}",claimId, savedPolicy.getPolicyNumber());
        }else {
            savedClaim.setRejectionReason("No Rejection Reason");
        }

        claimRepository.save(savedClaim);

        return claimMapper.toClaimResponse(savedClaim);
    }
}
