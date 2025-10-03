package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import com.hashjosh.notification.service.AgricultureRegistrationNotificationService;
import com.hashjosh.notification.service.FarmerRegistrationNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrationEventConsumer {

    private final FarmerRegistrationNotificationService farmerRegistrationNotificationService;
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

    @KafkaListener(topics = "farmer-event", groupId = "notification-service")
    public void consumeFarmerRegistrationEvent(@Payload FarmerRegistrationContract farmerRegistrationContract) {
        try {
            log.info("Processing farmer registration event: {}", farmerRegistrationContract);
            farmerRegistrationNotificationService.sendFarmerRegistrationEmailNotification(farmerRegistrationContract);
        }catch (Exception e) {
            log.error("❌ Failed to process application event: {}", farmerRegistrationContract, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }

}
