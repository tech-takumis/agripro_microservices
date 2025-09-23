package com.hashjosh.workflow.utils;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.workflow.exceptions.WorkflowException;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaUtils {

    private final WorkflowStatusRepository workflowStatusRepository;

    /**
     * Handle general application events (non-creation).
     *
     * @param contract the application contract coming from Kafka
     */
    public void handleApplicationEvent(ApplicationSubmissionContract contract) {
        WorkflowStatusHistory savedWorkflow = workflowStatusRepository
                .findByEventId(contract.getEventId())
                .orElseThrow(() -> new WorkflowException(
                        "Workflow status history not found for event ID " + contract.getEventId(),
                        HttpStatus.NOT_FOUND.value()
                ));

        try {
            EventType eventType = contract.getEventType();

            switch (eventType) {
                case
                    // Verification events
                     APPLICATION_APPROVED_BY_MA,
                     APPLICATION_REJECTED_BY_MA,
                     APPLICATION_APPROVED_BY_AEW,
                     APPLICATION_REJECTED_BY_AEW,

                     // Underwriter events
                     UNDER_REVIEW_BY_UNDERWRITER,
                     APPLICATION_APPROVED_BY_UNDERWRITER,
                     APPLICATION_REJECTED_BY_UNDERWRITER,

                     // Adjuster events
                     UNDER_REVIEW_BY_ADJUSTER,
                     APPLICATION_APPROVED_BY_ADJUSTER,
                     APPLICATION_REJECTED_BY_ADJUSTER,

                     // Insurance service events
                     POLICY_ISSUED,
                     CLAIM_APPROVED -> {
                    ApplicationStatus contractStatus = contract.getStatus();

                    savedWorkflow.setStatus(contractStatus);
                    savedWorkflow.setUpdatedBy(contract.getUploadedBy());
                    savedWorkflow.setUpdatedAt(LocalDateTime.now());

                    workflowStatusRepository.save(savedWorkflow);

                    log.info("✅ Updated workflow for application ID {} to status {}", 
                        savedWorkflow.getApplicationId(), savedWorkflow.getStatus());
                }
                default -> log.warn("Unsupported event type: {}", contract.getEventType());
            }
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid event type: {}", contract.getEventType(), e);
        }
    }

    /**
     * Create a workflow for the `APPLICATION_SUBMITTED` event type.
     *
     * @param contract the application contract containing the event data
     */
    public void createWorkflowForApplicationSubmission(ApplicationSubmissionContract contract) {
        try {
            // Check if a workflow already exists for this application
            boolean workflowExists = workflowStatusRepository.existsByApplicationId(contract.getApplicationId());
            if (workflowExists) {
                log.warn("Workflow already exists for application ID: {}", contract.getApplicationId());
                return;
            }

            WorkflowStatusHistory newWorkflow = WorkflowStatusHistory.builder()
                    .eventId(contract.getEventId())
                    .applicationId(contract.getApplicationId())
                    .status(ApplicationStatus.SUBMITTED) // Default status for a new workflow
                    .updatedBy(contract.getUploadedBy())
                    .updatedAt(LocalDateTime.now())
                    .version(contract.getVersion() != null ? contract.getVersion() : 0L)
                    .build();

            workflowStatusRepository.saveAndFlush(newWorkflow);

            log.info("✅ Created new workflow for application ID {} with status SUBMITTED", 
                    newWorkflow.getApplicationId());
        } catch (Exception e) {
            log.error("❌ Failed to create workflow for application ID {}: {}", 
                    contract.getApplicationId(), e.getMessage(), e);
            throw new WorkflowException("Failed to create workflow for application ID " + contract.getApplicationId(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}