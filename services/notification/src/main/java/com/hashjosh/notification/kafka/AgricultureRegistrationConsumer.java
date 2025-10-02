package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.notification.service.AgricultureRegistrationNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgricultureRegistrationConsumer {

    private final AgricultureRegistrationNotificationService agricultureRegistrationNotificationService;

    @KafkaListener(topics = "agriculture-event", groupId = "notification-service" )
    public void consumeAgricultureRegistrationEvent(AgricultureRegistrationContract event){
        try {
            log.info("Processing agriculture registration event: {}", event);
            agricultureRegistrationNotificationService.sendAgricultureRegistrationEmailNotification(event);
        }catch (Exception e) {
            log.error("❌ Failed to process agriculture event: {}", event, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }
}
