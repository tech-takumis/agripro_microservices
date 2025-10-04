package com.example.agriculture.repository;

import com.example.agriculture.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, UUID> {

    Optional<VerificationRecord> findBySubmissionId(UUID submissionId);
}
