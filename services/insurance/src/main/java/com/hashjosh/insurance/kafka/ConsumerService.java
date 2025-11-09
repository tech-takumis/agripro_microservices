package com.hashjosh.insurance.kafka;

import com.hashjosh.constant.pcic.enums.InsuranceStatus;
import com.hashjosh.insurance.entity.Insurance;
import com.hashjosh.insurance.repository.InsuranceRepository;
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
    private final InsuranceRepository insuranceRepository;
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

            Insurance insurance = Insurance.builder()
                    .submissionId(event.getSubmissionId())
                    .submittedBy(event.getUserId())
                    .status(InsuranceStatus.PENDING)
                    .build();

           insuranceRepository.save(insurance);

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

        Insurance insurance = Insurance.builder()
                .submissionId(event.getSubmissionId())
                .submittedBy(event.getUserId())
                .status(InsuranceStatus.PENDING)
                .build();

        insuranceRepository.save(insurance);

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
