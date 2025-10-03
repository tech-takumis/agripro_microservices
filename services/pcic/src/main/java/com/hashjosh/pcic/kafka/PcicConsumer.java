package com.hashjosh.pcic.kafka;


import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import com.hashjosh.pcic.entity.Claim;
import com.hashjosh.pcic.entity.ValidationRecord;
import com.hashjosh.pcic.enums.ClaimStatus;
import com.hashjosh.pcic.enums.ValidationStatus;
import com.hashjosh.pcic.repository.ClaimRepository;
import com.hashjosh.pcic.repository.ValidationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PcicConsumer {

    private final ValidationRecordRepository validationRecordRepository;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationEvent(InspectionCompletedEvent event) {
        log.info("Application event received: {}", event);

        ValidationRecord record = ValidationRecord.builder()
                .submissionId(event.getSubmissionId())
                .status(ValidationStatus.PENDING)
                .comments(null)
                .build();


        validationRecordRepository.save(record);
    }

}
