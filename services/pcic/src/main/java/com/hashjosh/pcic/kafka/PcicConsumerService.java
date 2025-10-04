package com.hashjosh.pcic.kafka;

import com.hashjosh.kafkacommon.application.VerificationCompletedEvent;
import com.hashjosh.pcic.entity.InspectionRecord;
import com.hashjosh.pcic.enums.InspectionStatus;
import com.hashjosh.pcic.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PcicConsumerService {
    private final InspectionRecordRepository inspectionRepository;

    @KafkaListener(topics = "application-lifecycle", groupId = "pcic-group")
    public void handleVerificationCompleted(VerificationCompletedEvent event) {
        if (!event.getStatus().equals("COMPLETED")) {
            log.info("Skipping rejected verification: {}", event.getSubmissionId());
            return;
        }
        InspectionRecord record = InspectionRecord.builder()
                .submissionId(event.getSubmissionId())
                .status(InspectionStatus.PENDING)
                .build();
        inspectionRepository.save(record);
    }

}
