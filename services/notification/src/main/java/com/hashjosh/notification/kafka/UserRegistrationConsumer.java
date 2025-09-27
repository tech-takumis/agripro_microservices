package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.user.UserRegistrationContract;
import com.hashjosh.notification.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationConsumer {

    private final UserRegistrationService userRegistrationService;

    @KafkaListener(topics = "user-event")
    public void userRegistrationEvent(UserRegistrationContract userRegistrationContract) {
        try {
            log.info("Processing user registration event: {}", userRegistrationContract);

            userRegistrationService.sendUserRegistrationEmailNotification(userRegistrationContract);
        }catch (Exception e) {
            log.error("❌ Failed to process application event: {}", userRegistrationContract, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }

    }
}
