package com.hashjosh.insurance.repository;

import com.hashjosh.insurance.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, UUID> {
    Optional<InspectionRecord> findBySubmissionId(UUID submissionId);
}
