package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import com.hashjosh.notification.service.FarmerRegistrationNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FarmerRegistrationConsumer {

    private final FarmerRegistrationNotificationService farmerRegistrationNotificationService;

    @KafkaListener(topics = "farmer-event")
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
