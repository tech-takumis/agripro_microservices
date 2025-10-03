package com.hashjosh.notification.kafka;

import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import com.hashjosh.notification.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationEventConsumer {

    private final ApplicationService applicationService;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationSubmissionEvent(ApplicationSubmittedEvent event) {
        try {
            log.info("Processing application event: {}", event);

            applicationService.sendEmailNotification(event);
        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", event, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }
}
