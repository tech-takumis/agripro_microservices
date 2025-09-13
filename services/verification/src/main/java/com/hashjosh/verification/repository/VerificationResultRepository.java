package com.hashjosh.verification.repository;

import com.hashjosh.verification.model.VerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationResultRepository extends JpaRepository<VerificationResult, UUID> {

    Optional<VerificationResult> findByApplicationId(UUID applicationId);

    List<VerificationResult> findByApprovedByAdjuster(boolean approvedByAdjuster);
    List<VerificationResult> findByVerifiedByUnderwriter(boolean verifiedByAdjuster);

    List<VerificationResult> findAllByApprovedByAdjusterAndVerifiedByUnderwriter(boolean approvedByAdjuster, boolean verifiedByUnderwriter);

}
