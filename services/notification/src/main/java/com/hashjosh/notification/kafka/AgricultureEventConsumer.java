package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.agriculture.BatchCreatedEvent;
import com.hashjosh.notification.service.AgricultureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgricultureEventConsumer {

    private final AgricultureService agricultureService;

    @KafkaListener(topics = "batch-created", groupId = "notification-service" )
    public void consumeBatchCreatedEvent(BatchCreatedEvent event) {
        try {
            log.info("Processing batch created event: {}", event);
            // Here you would call a method in agricultureService to handle the event
             agricultureService.handleBatchCreatedEvent(event);
        } catch (Exception e) {
            log.error("❌ Failed to process batch created event: {}", event, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }
}
