package com.hashjosh.notification.kafka;

import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
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

    @KafkaListener(topics = "application-events", groupId = "application-submission-event")
    public void consumeApplicationSubmissionEvent(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            log.info("Processing application event: {}", applicationSubmissionContract);

            if (EventType.APPLICATION_SUBMITTED == applicationSubmissionContract.getEventType()) {
                applicationService.sendEmailNotification(applicationSubmissionContract);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationSubmissionContract, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }
}
