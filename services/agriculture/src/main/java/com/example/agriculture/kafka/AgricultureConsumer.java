package com.example.agriculture.kafka;

import com.example.agriculture.entity.InspectionRecord;
import com.example.agriculture.mapper.InspectionRecordMapper;
import com.example.agriculture.repository.InspectionRecordResultRepository;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgricultureConsumer {

    private final InspectionRecordMapper inspectionRecordMapper;
    private final InspectionRecordResultRepository inspectionRecordResultRepository;

    @KafkaListener(topics = "application-events", groupId = "verification-service")
    public void consumeSubmittedApplication(@Payload ApplicationSubmittedEvent event) {
        try {
            InspectionRecord result = inspectionRecordMapper.toVerificationResult(event);
            inspectionRecordResultRepository.save(result);
        } catch (Exception ex) {
            log.error("❌ Failed to process event: {}", event, ex);
            throw ex; // ✅ Let Spring Kafka retry or send to DLT
        }
    }


}
