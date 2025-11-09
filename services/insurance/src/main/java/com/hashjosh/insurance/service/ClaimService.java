package com.hashjosh.insurance.service;


import com.hashjosh.insurance.dto.claim.ClaimRequest;
import com.hashjosh.insurance.dto.claim.ClaimResponse;
import com.hashjosh.insurance.entity.Claim;
import com.hashjosh.insurance.exception.ApiException;
import com.hashjosh.insurance.mapper.ClaimMapper;
import com.hashjosh.insurance.repository.ClaimRepository;
import jakarta.transaction.Transactional;
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
    private final ClaimMapper claimMapper;

    @Transactional
    public ClaimResponse updateClaim(UUID claimId,
                                     ClaimRequest claim) {
        Claim savedClaim = claimRepository.findById(claimId)
                .orElseThrow(() -> ApiException.notFound("Claim not found"));

        savedClaim.setClaimAmount(claim.claimAmount());
        savedClaim.setPayoutStatus(claim.payoutStatus());

        claimRepository.save(savedClaim);

        return claimMapper.toClaimResponse(savedClaim);
    }

    @Transactional
    public ClaimResponse createClaim(ClaimRequest request) {

        Claim claim = claimMapper.toClaimEntity(request);

        return  claimMapper.toClaimResponse(claim);
    }

    @Transactional
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(claimMapper::toClaimResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID claimId) {
        claimRepository.deleteById(claimId);
    }
}
