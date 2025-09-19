package com.hashjosh.application.service;

import com.hashjosh.application.dto.BatchApplicationResponse;
import com.hashjosh.application.dto.BatchResponse;
import com.hashjosh.application.mapper.ApplicationBatchMapper;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationBatch;
import com.hashjosh.application.enums.BatchStatus;
import com.hashjosh.application.repository.ApplicationBatchRepository;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.jwtshareable.service.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationBatchService {

    private final ApplicationBatchRepository batchRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationBatchMapper applicationBatchMapper;

    @Transactional
    public BatchResponse createBatch(String token,String tenantId, String batchName, UUID createdBy, List<UUID> applicationIds) {
        TenantContext.setTenantId(tenantId);
        try {
            List<Application> applications = applicationRepository.findAllById(applicationIds);
            if (applications.size() != applicationIds.size()) {
                throw new IllegalArgumentException("Some application IDs are invalid");
            }

            ApplicationBatch batch = ApplicationBatch.builder()
                    .tenantId(tenantId)
                    .name(batchName)
                    .status(BatchStatus.PENDING)
                    .createdBy(createdBy)
                    .applications(applications)
                    .build();

            applications.forEach(app -> app.setBatch(batch));


            return  applicationBatchMapper.toApplicationBatchResponse(batchRepository.save(batch), token);

        } finally {
            TenantContext.clear();
        }
    }

    @Transactional
    public ApplicationBatch updateBatchStatus(UUID batchId, BatchStatus status, String tenantId) {
        TenantContext.setTenantId(tenantId);
        try {
            ApplicationBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
            batch.setStatus(status);
            return batchRepository.save(batch);
        } finally {
            TenantContext.clear();
        }
    }

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"applications"})
    public List<ApplicationBatch> getBatchesByTenant(String tenantId) {
        TenantContext.setTenantId(tenantId);
        try {
            return batchRepository.findByTenantId(tenantId);
        } finally {
            TenantContext.clear();
        }
    }
}
