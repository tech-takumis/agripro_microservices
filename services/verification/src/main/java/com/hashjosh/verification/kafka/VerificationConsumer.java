package com.hashjosh.verification.kafka;

import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.verification.mapper.VerificationMapper;
import com.hashjosh.verification.model.VerificationResult;
import com.hashjosh.verification.repository.VerificationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationConsumer {

    private final VerificationMapper verificationMapper;
    private final VerificationResultRepository verificationResultRepository;

    @KafkaListener(topics = "application-events", groupId = "verification-service")
    public void consumeSubmittedApplication(ApplicationSubmissionContract event) {
        try {
            if (EventType.APPLICATION_SUBMITTED == event.getEventType()) {
                handleSubmitted(event);
            } else {
                log.debug("Ignoring unsupported event type: {}", event.getEventType());
            }
        } catch (Exception ex) {
            log.error("❌ Failed to process event: {}", event, ex);
            throw ex; // ✅ Let Spring Kafka retry or send to DLT
        }
    }

    private void handleSubmitted(ApplicationSubmissionContract applicationSubmissionContract) {

        VerificationResult result = verificationMapper.toVerificationResult(applicationSubmissionContract);

        verificationResultRepository.save(result);
    }


}
