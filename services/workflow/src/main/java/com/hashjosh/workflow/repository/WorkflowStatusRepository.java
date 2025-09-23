package com.hashjosh.workflow.repository;


import com.hashjosh.workflow.model.WorkflowStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatusHistory, UUID> {
    List<WorkflowStatusHistory> findByApplicationId(UUID applicationId);

    WorkflowStatusHistory findByApplicationIdAndUpdatedBy(UUID applicationId, UUID updatedBy);

    Optional<WorkflowStatusHistory> findByEventId(UUID eventId);

    boolean existsByApplicationId(UUID applicationId);
}
