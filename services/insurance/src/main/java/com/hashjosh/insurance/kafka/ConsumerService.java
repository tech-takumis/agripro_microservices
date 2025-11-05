package com.hashjosh.insurance.kafka;

import com.hashjosh.insurance.entity.InspectionRecord;
import com.hashjosh.insurance.repository.InspectionRecordRepository;
import com.hashjosh.kafkacommon.application.*;
import com.hashjosh.constant.pcic.enums.InspectionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {
    private final InspectionRecordRepository inspectionRepository;
    private final KafkaProducer producer;

    @KafkaListener(topics = "application-submitted", groupId = "pcic-application-submitted-group")
    public void listenApplicationSubmitted(@Payload ApplicationSubmittedEvent event) {
        handleApplicationSubmittedEvent(event);
    }

    @KafkaListener(topics = "application-forwarded", groupId = "pcic-application-forwarded-group")
    public void listenApplicationForwarded(@Payload ApplicationForwarded event) {
        handleApplicationForwarded(event);
    }

    private void handleApplicationSubmittedEvent(ApplicationSubmittedEvent event) {
        if("PCIC".equalsIgnoreCase(event.getProvider())){
           InspectionRecord record = InspectionRecord.builder()
                   .submittedBy(event.getUserId())
                   .submissionId(event.getSubmissionId())
                   .status(InspectionStatus.PENDING)
                   .build();

           inspectionRepository.save(record);

           ApplicationReceived receivedEvent = ApplicationReceived.builder()
                   .provider("PCIC")
                   .userId(event.getUserId())
                   .submissionId(event.getSubmissionId())
                   .status(InspectionStatus.PENDING.name())
                   .build();

           producer.publishEvent("application-lifecycle", receivedEvent);

            log.info("Application {} sent to PCIC for inspection", event.getSubmissionId());
        }
    }

    private void handleApplicationForwarded(ApplicationForwarded event) {

        InspectionRecord record = InspectionRecord.builder()
                .submissionId(event.getSubmissionId())
                .status(InspectionStatus.PENDING)
                .submittedBy(event.getUserId())
                .build();
        inspectionRepository.save(record);

        producer.publishEvent("application-received",
        ApplicationReceived.builder()
                .provider("PCIC")
                .userId(event.getUserId())
                .submissionId(event.getSubmissionId())
                .status(InspectionStatus.PENDING.name())
                .build());

        log.info("Application {} received by PCIC", event.getSubmissionId());
    }
}
