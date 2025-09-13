package com.hashjosh.verification.kafka;

import com.hashjosh.kafkacommon.application.ApplicationContract;
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
    public void consumeSubmittedApplication(ApplicationContract applicationContract) {
        try {
            log.info("Consume application submission event: {}", applicationContract);

            if (applicationContract.eventType().equals("application-submitted")) {
                handleSubmitted(applicationContract);
            } else {
                log.debug("Ignoring unknown event type {}", applicationContract.eventType());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationContract, e);
            throw e; // ✅ Let Spring Kafka handle retry + DLT
        }
    }

    private void handleSubmitted(ApplicationContract applicationContract) {

        VerificationResult result = verificationMapper.toVerificationResult(applicationContract);

        verificationResultRepository.save(result);
    }


}
