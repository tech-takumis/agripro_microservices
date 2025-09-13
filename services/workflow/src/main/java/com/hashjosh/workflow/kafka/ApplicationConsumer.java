package com.hashjosh.workflow.kafka;

import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.verification.VerificationContract;
import com.hashjosh.workflow.enums.ApplicationStatus;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import com.hashjosh.workflow.utils.KafkaUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ApplicationConsumer {
    private final KafkaUtils kafkaUtils;

    @PostConstruct
    public void check() {
        log.info("✅ Kafka consumer config loaded, groupId=workflow-service");
    }


    @KafkaListener(topics = "application-events")
    public void consumeSubmittedApplication(ApplicationContract applicationContract) {
        try {
            log.info("Consume application submission event: {}", applicationContract);

           switch (applicationContract.eventType()){
                case "application-submitted" -> kafkaUtils.handleSubmitted(applicationContract);
                case "application-verified" -> kafkaUtils.handleVerified(applicationContract);
                case "application-rejected" -> kafkaUtils.handleRejected(applicationContract);
                default -> log.debug("Ignoring unknown event type {}", applicationContract.eventType());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationContract, e);
            throw e; // ✅ Let Spring Kafka handle retry + DLT
        }
    }



}
