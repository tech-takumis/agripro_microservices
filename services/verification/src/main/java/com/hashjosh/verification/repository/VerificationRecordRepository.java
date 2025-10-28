package com.hashjosh.verification.repository;

import com.hashjosh.constant.verification.VerificationStatus;
import com.hashjosh.verification.entity.Batch;
import com.hashjosh.verification.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, UUID> {

    Optional<VerificationRecord> findBySubmissionId(UUID submissionId);

    List<VerificationRecord> findByStatus(VerificationStatus verificationStatus);
}
