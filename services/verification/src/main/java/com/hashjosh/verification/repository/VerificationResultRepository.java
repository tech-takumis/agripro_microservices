package com.hashjosh.verification.repository;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.verification.model.VerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationResultRepository extends JpaRepository<VerificationResult, UUID> {

    Optional<VerificationResult> findByApplicationId(UUID applicationId);
    
    List<VerificationResult> findByStatus(ApplicationStatus status);
    
    List<VerificationResult> findByStatusIn(List<ApplicationStatus> statuses);
    
    List<VerificationResult> findByStatusInAndInspectionType(
        List<ApplicationStatus> statuses,
        String inspectionType
    );
    
    List<VerificationResult> findByApplicationIdIn(List<UUID> applicationIds);
}
