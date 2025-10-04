package com.hashjosh.pcic.kafka;

import com.hashjosh.kafkacommon.application.ApplicationReceivedByPcicEvent;
import com.hashjosh.kafkacommon.application.ApplicationSentToPcicEvent;
import com.hashjosh.kafkacommon.application.VerificationCompletedEvent;
import com.hashjosh.pcic.entity.InspectionRecord;
import com.hashjosh.constant.pcic.enums.InspectionStatus;
import com.hashjosh.pcic.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PcicConsumerService {
    private final InspectionRecordRepository inspectionRepository;
    private final PcicProducer producer;

    @KafkaListener(topics = "application-lifecycle", groupId = "pcic-group")
    public void handleVerificationCompleted(ApplicationSentToPcicEvent event) {

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

}
