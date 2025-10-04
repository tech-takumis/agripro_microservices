package com.hashjosh.pcic.repository;

import com.hashjosh.pcic.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, UUID> {
    Optional<InspectionRecord> findBySubmissionId(UUID submissionId);
}
