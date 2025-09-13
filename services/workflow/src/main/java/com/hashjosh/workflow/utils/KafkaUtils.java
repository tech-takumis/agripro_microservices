package com.hashjosh.workflow.utils;

import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.verification.VerificationContract;
import com.hashjosh.workflow.enums.ApplicationStatus;
import com.hashjosh.workflow.exceptions.WorkflowHistoryAlreadyExist;
import com.hashjosh.workflow.exceptions.WorkflowHistoryNotFoundException;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaUtils {

    private final WorkflowStatusRepository workflowStatusRepository;


    public void handleRejected(ApplicationContract contract) {
        WorkflowStatusHistory savedWorkflow = workflowStatusRepository
                .findByEventId(contract.eventId())
                .orElseThrow(() -> new WorkflowHistoryNotFoundException(
                        "Workflow status history not found for event id " + contract.eventId(),
                        HttpStatus.NOT_FOUND.value(),
                        "kafka handle verified function event"
                ));

        savedWorkflow.setStatus(ApplicationStatus.REJECTED);
        savedWorkflow.setUpdatedBy(contract.payload().userId());
        savedWorkflow.setVersion(savedWorkflow.getVersion());

        WorkflowStatusHistory history = workflowStatusRepository.save(savedWorkflow);

        log.info("✅ Rejected workflow history for application id {} status:: {}", history.getApplicationId(), history.getStatus());

    }


    public void handleVerified(ApplicationContract contract) {

        WorkflowStatusHistory savedWorkflow = workflowStatusRepository
                .findByEventId(contract.eventId())
                        .orElseThrow(() -> new WorkflowHistoryNotFoundException(
                            "Workflow status history not found for event id " + contract.eventId(),
                                HttpStatus.NOT_FOUND.value(),
                                "kafka handle verified function event"
                        ));

        savedWorkflow.setStatus(ApplicationStatus.valueOf(contract.payload().status()));
        savedWorkflow.setUpdatedBy(contract.payload().userId());
        Long contractVersion = contract.payload().version();
        if (savedWorkflow.getVersion() != null && savedWorkflow.getVersion().equals(contractVersion)) {
            savedWorkflow.setVersion(contractVersion);
        } else {
            // If versions differ, let Hibernate handle the increment
            log.warn("Version mismatch detected for eventId {}, using current version {}", contract.eventId(), savedWorkflow.getVersion());
        }
        WorkflowStatusHistory history = workflowStatusRepository.save(savedWorkflow);

        log.info("✅ Verified workflow history for application id {} status:: {}", history.getApplicationId(), history.getStatus());

    }

    // Done implementation of application submitted event
    public void handleSubmitted(ApplicationContract contract) {
        var workflowHistory = new WorkflowStatusHistory();

        Optional<WorkflowStatusHistory> optionalWorkflow = workflowStatusRepository
                .findByEventId(contract.eventId());

        if(optionalWorkflow.isPresent()) {
            log.info("Event already exist for event id {}", contract.eventId());
            throw new WorkflowHistoryAlreadyExist(
                "Workflow event Id "+ contract.eventId() + " already exist!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        workflowHistory.setEventId(contract.eventId());
        workflowHistory.setApplicationId(contract.applicationId());
        workflowHistory.setStatus(ApplicationStatus.SUBMITTED);
        workflowHistory.setUpdatedBy(contract.payload().userId());
        workflowHistory.setVersion(contract.payload().version());

        WorkflowStatusHistory history = workflowStatusRepository.save(workflowHistory);

        log.info("✅ Application submitted workflow history for application id {} status:: {}",  history.getApplicationId(), history.getStatus());

    }

    // TODO: Not implemented yet
    public void handleUpdate(VerificationContract contract) {
        log.info("Application update workflow history not implemented yet!");
        return;
    }


    // TODO: Not implemented yet
    public void handleDeleted(ApplicationContract contract) {
        log.info("Application deleted workflow history not implemented yet!");
          return;
    }
}
