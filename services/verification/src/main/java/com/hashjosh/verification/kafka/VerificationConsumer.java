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

    @KafkaListener(topics = "application-events")
    public void consumeSubmittedApplication(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            log.info("Consume application submission event: {}", applicationSubmissionContract);

            if (EventType.APPLICATION_SUBMITTED == applicationSubmissionContract.getEventType()) {
                handleSubmitted(applicationSubmissionContract);
            } else {
                log.debug("Ignoring unknown event type {}", applicationSubmissionContract.getEventType());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationSubmissionContract, e);
            throw e; // ✅ Let Spring Kafka handle retry + DLT
        }
    }

    private void handleSubmitted(ApplicationSubmissionContract applicationSubmissionContract) {

        VerificationResult result = verificationMapper.toVerificationResult(applicationSubmissionContract);

        verificationResultRepository.save(result);
    }


}
