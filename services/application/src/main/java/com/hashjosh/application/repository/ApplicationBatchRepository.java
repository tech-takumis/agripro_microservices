package com.hashjosh.application.repository;


import com.hashjosh.application.model.ApplicationBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationBatchRepository extends JpaRepository<ApplicationBatch, UUID>{
    List<ApplicationBatch> findByTenantId(String tenantId);
}
