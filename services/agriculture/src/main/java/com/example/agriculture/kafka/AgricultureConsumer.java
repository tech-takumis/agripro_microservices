package com.example.agriculture.kafka;

import com.example.agriculture.entity.VerificationRecord;
import com.hashjosh.constant.verification.VerificationStatus;
import com.example.agriculture.repository.VerificationRecordRepository;
import com.hashjosh.kafkacommon.application.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgricultureConsumer {

    private final VerificationRecordRepository verificationRecordRepository;
    private final AgricultureProducer agricultureProducer;

    @KafkaListener(topics = "application-lifecycle", groupId = "pcic-group")
    public void listen(@Payload ApplicationSubmittedEvent event) {
        handleApplicationSubmittedEvent(event);
    }

    private void handleApplicationSubmittedEvent(@Payload ApplicationSubmittedEvent event) {
        try {
            if ("Agriculture".equalsIgnoreCase(event.getProvider())) {
                VerificationRecord record = VerificationRecord.builder()
                        .submissionId(event.getSubmissionId())
                        .uploadedBy(event.getUserId())
                        .status(VerificationStatus.PENDING)
                        .verificationType("Application Verification")
                        .build();
                verificationRecordRepository.save(record);

                agricultureProducer.publishEvent("application-lifecycle",
                        ApplicationReceived.builder()
                                .submissionId(event.getSubmissionId())
                                .provider(event.getProvider())
                                .userId(event.getUserId())
                                .status(VerificationStatus.PENDING.name())
                                .build()
                );

                log.info("Verification started for application: {}", event.getSubmissionId());
            }
        } catch (Exception e) {
            log.error("Failed to process ApplicationSubmittedEvent: {}", event.getSubmissionId(), e);
            throw e;
        }
    }
}
