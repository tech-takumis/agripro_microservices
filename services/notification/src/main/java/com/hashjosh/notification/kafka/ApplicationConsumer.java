 package com.hashjosh.notification.kafka;

 import com.hashjosh.constant.EventType;
 import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
 import com.hashjosh.notification.service.EmailNotificationService;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.kafka.annotation.KafkaListener;
 import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationConsumer {
    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationEvent(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            log.info("Processing application event: {}", applicationSubmissionContract);
            
            if (EventType.APPLICATION_SUBMITTED == applicationSubmissionContract.getEventType()) {
                emailNotificationService.sendEmailNotification(applicationSubmissionContract);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationSubmissionContract, e);
            // Consider adding retry logic or dead-letter queue handling here
            throw e;
        }
    }

}
