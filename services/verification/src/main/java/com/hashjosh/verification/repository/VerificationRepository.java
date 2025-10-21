package com.hashjosh.verification.repository;

import com.hashjosh.verification.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationRecord, UUID> {
    List<VerificationRecord> findBySubmissionId(UUID submissionId);
    List<VerificationRecord> findByUploadedBy(UUID uploadedBy);
}