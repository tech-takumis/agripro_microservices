package com.example.agriculture.repository;

import com.example.agriculture.entity.InspectionRecord;
import com.hashjosh.constant.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectionRecordResultRepository extends JpaRepository<InspectionRecord, UUID> {

    Optional<InspectionRecord> findByApplicationId(UUID applicationId);
    
    List<InspectionRecord> findByStatus(ApplicationStatus status);
    
    List<InspectionRecord> findByStatusIn(List<ApplicationStatus> statuses);
    
    List<InspectionRecord> findByStatusInAndInspectionType(
        List<ApplicationStatus> statuses,
        String inspectionType
    );
}
