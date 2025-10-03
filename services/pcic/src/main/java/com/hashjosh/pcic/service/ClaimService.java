package com.hashjosh.pcic.service;


import com.hashjosh.pcic.dto.*;
import com.hashjosh.pcic.entity.*;
import com.hashjosh.pcic.enums.ClaimStatus;
import com.hashjosh.pcic.enums.PolicyStatus;
import com.hashjosh.pcic.exception.ClaimNotFoundException;
import com.hashjosh.pcic.mapper.ClaimMapper;
import com.hashjosh.pcic.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
                        HttpStatus.NOT_FOUND.value()
                ));

        savedClaim.setClaimAmount(claim.claimAmount());
        savedClaim.setPayoutStatus(claim.payoutStatus());

        Policy policy = new Policy();

        policy.setSubmissionId(savedClaim.getSubmissionId());
        policy.setPolicyNumber(UUID.randomUUID().toString());
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setCoverageAmount(savedClaim.getClaimAmount());

        Policy savedPolicy = policyRepository.save(policy);

        claimRepository.save(savedClaim);

        return claimMapper.toClaimResponse(savedClaim);
    }
}
