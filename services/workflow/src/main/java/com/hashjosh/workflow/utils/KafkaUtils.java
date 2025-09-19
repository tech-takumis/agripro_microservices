package com.hashjosh.workflow.utils;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.workflow.exceptions.WorkflowHistoryNotFoundException;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaUtils {

    private final WorkflowStatusRepository workflowStatusRepository;

    public void handleApplicationEvent(ApplicationContract contract) {
        WorkflowStatusHistory savedWorkflow = workflowStatusRepository
                .findByEventId(contract.getEventId())
                .orElseThrow(() -> new WorkflowHistoryNotFoundException(
                        "Workflow status history not found for event id " + contract.getEventId(),
                        HttpStatus.NOT_FOUND.value(),
                        "kafka handle verified function event"
                ));

        try {
            // Parse the event type from the contract and process accordingly
            EventType eventType = EventType.fromString(contract.getEventType());

            switch (eventType) {
                case APPLICATION_SUBMITTED, APPLICATION_APPROVED_BY_MA, APPLICATION_REJECTED_BY_MA,
                     APPLICATION_APPROVED_BY_AEW, APPLICATION_REJECTED_BY_AEW -> {
                    // Map contract status to ApplicationStatus
                    String contractStatus = contract.getPayload().getStatus();
                    ApplicationStatus newStatus = mapContractStatusToApplicationStatus(contractStatus);

                    savedWorkflow.setStatus(newStatus);
                    savedWorkflow.setUpdatedBy(contract.getPayload().getUserId());

                    Long contractVersion = contract.getPayload().getVersion();
                    if (savedWorkflow.getVersion() != null && savedWorkflow.getVersion().equals(contractVersion)) {
                        savedWorkflow.setVersion(contractVersion);
                    } else {
                        // If versions differ, let Hibernate handle the increment
                        log.warn("Version mismatch detected for eventId {}, using current version {}",
                                contract.getEventId(), savedWorkflow.getVersion());
                    }

                    WorkflowStatusHistory history = workflowStatusRepository.save(savedWorkflow);
                    log.info("✅ Updated workflow history for application id {} with status:: {}",
                            history.getApplicationId(), history.getStatus());
                }
                default -> log.warn("Unsupported event type received: {}", eventType);
            }
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid event type received: {}", contract.getEventType(), e);
        }
    }

    private ApplicationStatus mapContractStatusToApplicationStatus(String contractStatus) {
        if (contractStatus == null) {
            return ApplicationStatus.PENDING;
        }

        try {
            // Map contract statuses to ApplicationStatus enum values
            return switch (contractStatus) {
                case "SUBMITTED" -> ApplicationStatus.SUBMITTED;
                case "PENDING" -> ApplicationStatus.PENDING;
                case "APPROVED" -> ApplicationStatus.APPROVED;
                case "VERIFIED" -> ApplicationStatus.VERIFIED;
                case "REJECTED" -> ApplicationStatus.REJECTED;
                case "CANCELLED" -> ApplicationStatus.CANCELLED;
                case "CANCELLED_BY_USER" -> ApplicationStatus.CANCELLED_BY_USER;

                // Verification Service statuses
                case "APPROVED_BY_MA" -> ApplicationStatus.APPROVED_BY_MA;
                case "REJECTED_BY_MA" -> ApplicationStatus.REJECTED_BY_MA;
                case "APPROVED_BY_AEW" -> ApplicationStatus.APPROVED_BY_AEW;
                case "REJECTED_BY_AEW" -> ApplicationStatus.REJECTED_BY_AEW;

                // Underwriter statuses
                case "UNDER_REVIEW_BY_UNDERWRITER" -> ApplicationStatus.UNDER_REVIEW_BY_UNDERWRITER;
                case "APPROVED_BY_UNDERWRITER" -> ApplicationStatus.APPROVED_BY_UNDERWRITER;
                case "REJECTED_BY_UNDERWRITER" -> ApplicationStatus.REJECTED_BY_UNDERWRITER;

                // Adjuster statuses
                case "UNDER_REVIEW_BY_ADJUSTER" -> ApplicationStatus.UNDER_REVIEW_BY_ADJUSTER;
                case "APPROVED_BY_ADJUSTER" -> ApplicationStatus.APPROVED_BY_ADJUSTER;
                case "REJECTED_BY_ADJUSTER" -> ApplicationStatus.REJECTED_BY_ADJUSTER;

                // Insurance Service statuses
                case "POLICY_ISSUED" -> ApplicationStatus.POLICY_ISSUED;
                case "CLAIM_APPROVED" -> ApplicationStatus.CLAIM_APPROVED;

                default -> {
                    log.warn("Unknown contract status: {}", contractStatus);
                    yield ApplicationStatus.PENDING; // Default to PENDING for unknown statuses
                }
            };
        } catch (IllegalArgumentException e) {
            log.warn("Error mapping contract status: {}. Defaulting to PENDING. Error: {}", contractStatus, e.getMessage());
            return ApplicationStatus.PENDING;
        }
    }
}