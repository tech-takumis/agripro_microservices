package com.example.agriculture.repository;

import com.example.agriculture.entity.VerificationRecord;
import com.hashjosh.constant.verification.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, UUID> {

    Optional<VerificationRecord> findBySubmissionId(UUID submissionId);

    List<VerificationRecord> findByStatus(VerificationStatus verificationStatus);
}
