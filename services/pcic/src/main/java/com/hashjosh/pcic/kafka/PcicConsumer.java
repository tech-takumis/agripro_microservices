package com.hashjosh.pcic.kafka;


import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import com.hashjosh.pcic.entity.InspectionRecord;
import com.hashjosh.pcic.enums.ValidationStatus;
import com.hashjosh.pcic.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PcicConsumer {

    private final InspectionRecordRepository inspectionRecordRepository;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationEvent(InspectionCompletedEvent event) {
        log.info("Application event received: {}", event);

        // NOTE: TO DO I THINK I NEED TO CHANGE THIS INSPECTION
    }

}
