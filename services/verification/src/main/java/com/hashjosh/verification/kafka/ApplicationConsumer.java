package com.hashjosh.verification.kafka;

import com.hashjosh.constant.verification.VerificationStatus;
import com.hashjosh.kafkacommon.application.ApplicationReceived;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import com.hashjosh.verification.entity.Batch;
import com.hashjosh.verification.entity.VerificationRecord;
import com.hashjosh.verification.repository.BatchRepository;
import com.hashjosh.verification.repository.VerificationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationConsumer {
    private final VerificationRecordRepository verificationRecordRepository;
    private final VerificationProducer publisher;
    private final BatchRepository batchRepository;

    @KafkaListener(topics = "application-lifecycle", groupId = "verification-group")
    public void listen(@Payload ApplicationSubmittedEvent event) {
        handleApplicationSubmittedEvent(event);
    }

    private void handleApplicationSubmittedEvent(@Payload ApplicationSubmittedEvent event) {
        try {
            if ("Agriculture".equalsIgnoreCase(event.getProvider())) {
                List<Batch> batches = batchRepository.findAllAvailableBatches();
                if (batches.isEmpty()) {
                    log.info("No available batches for application: {}", event.getSubmissionId());
                    return;
                }

                Batch selectedBatch = null;
                for (Batch batch : batches) {
                    boolean notFull = batch.getVerificationRecords().size() < batch.getMaxApplications();
                    boolean available = batch.isAvailable();

                    if(notFull && available) {
                        selectedBatch = batch;
                        break;
                    }
                }

                VerificationRecord record = VerificationRecord.builder()
                        .submissionId(event.getSubmissionId())
                        .uploadedBy(event.getUserId())
                        .batch(selectedBatch)
                        .status(VerificationStatus.PENDING)
                        .verificationType("Application Verification")
                        .build();
                verificationRecordRepository.save(record);

                publisher.publishEvent("application-lifecycle",
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
