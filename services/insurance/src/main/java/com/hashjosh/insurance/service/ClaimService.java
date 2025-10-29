package com.hashjosh.insurance.service;


import com.hashjosh.constant.pcic.enums.ClaimStatus;
import com.hashjosh.constant.pcic.enums.PolicyStatus;
import com.hashjosh.insurance.dto.claim.ClaimRequest;
import com.hashjosh.insurance.dto.claim.ClaimResponse;
import com.hashjosh.insurance.entity.Claim;
import com.hashjosh.insurance.entity.Policy;
import com.hashjosh.insurance.mapper.ClaimMapper;
import com.hashjosh.insurance.repository.ClaimRepository;
import com.hashjosh.insurance.repository.PolicyRepository;
import com.hashjosh.pcic.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .orElseThrow(() -> ApiException.notFound("Claim not found"));

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

    public Claim createClaim(UUID submissionId, UUID policyId) {
        return Claim.builder()
                .submissionId(submissionId)
                .policyId(policyId)
                .claimAmount(5000.0)
                .payoutStatus(ClaimStatus.PENDING)
                .build();
    }

    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(claimMapper::toClaimResponse)
                .toList();
    }
}
