package com.hashjosh.workflowservice.repository;


import com.hashjosh.workflowservice.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, Long> {
}
