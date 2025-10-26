package com.hashjosh.pcic.kafka;

import com.hashjosh.kafkacommon.application.ApplicationReceivedByPcicEvent;
import com.hashjosh.kafkacommon.application.ApplicationSentToPcicEvent;
import com.hashjosh.kafkacommon.application.VerificationCompletedEvent;
import com.hashjosh.kafkacommon.application.VerificationStartedEvent;
import com.hashjosh.pcic.entity.InspectionRecord;
import com.hashjosh.constant.pcic.enums.InspectionStatus;
import com.hashjosh.pcic.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PcicConsumerService {
    private final InspectionRecordRepository inspectionRepository;
    private final PcicProducer producer;

    @KafkaListener(topics = "application-lifecycle", groupId = "pcic-group")
    public void listen(@Payload Object event) {
        if (event instanceof ApplicationSentToPcicEvent) {
            handleApplicationSentToPcicEvent((ApplicationSentToPcicEvent) event);
        } else if (event instanceof VerificationStartedEvent) {
            handleVerificationStartedEvent((VerificationStartedEvent) event);
        } else if (event instanceof VerificationCompletedEvent) {
            handleVerificationCompletedEvent((VerificationCompletedEvent) event);
        } else {
            log.warn("Received unknown event type: {}", event.getClass().getName());
        }
    }

    private void handleApplicationSentToPcicEvent(ApplicationSentToPcicEvent event) {

        InspectionRecord record = InspectionRecord.builder()
                .submissionId(event.getSubmissionId())
                .status(InspectionStatus.PENDING)
                .submittedBy(event.getUserId())
                .build();
        inspectionRepository.save(record);

        producer.publishEvent("application-lifecycle",
        new ApplicationReceivedByPcicEvent(
                event.getSubmissionId(),
                event.getUserId(),
                InspectionStatus.PENDING.name(),
                LocalDateTime.now()
        ));
        log.info("Application {} received by PCIC", event.getSubmissionId());
    }

    private void handleVerificationStartedEvent(VerificationStartedEvent event) {
        // Add your handling logic for VerificationStartedEvent here
        log.info("Received VerificationStartedEvent for submissionId {} by user {} at {}", event.getSubmissionId(), event.getUserId(), event.getStartedAt());
    }

    private void handleVerificationCompletedEvent(VerificationCompletedEvent event) {
        // Add your handling logic for VerificationCompletedEvent here
        log.info("Received VerificationCompletedEvent for submissionId {} by user {} at {}", event.getSubmissionId(), event.getUserId(), event.getVerifiedAt());
    }
}
