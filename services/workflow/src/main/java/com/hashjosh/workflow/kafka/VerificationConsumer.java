package com.hashjosh.workflow.kafka;

import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.workflow.utils.KafkaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class VerificationConsumer {

    private final KafkaUtils kafkaUtils;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationEvent(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            log.info("Consume application event: {}", applicationSubmissionContract);

            // Parse the event type from the contract
            EventType eventType = applicationSubmissionContract.getEventType();

            // Handle application submission workflow creation separately
            if (isSupportedEvent(eventType)) {
                kafkaUtils.handleApplicationEvent(applicationSubmissionContract);
            } else {
                log.debug("Unsupported event type received: {}", applicationSubmissionContract.getEventType());
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid event type received: {}", applicationSubmissionContract.getEventType(), e);
        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationSubmissionContract, e);
            throw e; // ✅ Let Spring Kafka handle retry + DLT
        }
    }

    /**
     * Determines whether an event type is supported by the workflow service.
     *
     * @param eventType the event type to check
     * @return true if supported, false otherwise
     */
    private boolean isSupportedEvent(EventType eventType) {
        return switch (eventType) {
            case // Verification events
                    APPLICATION_APPROVED_BY_MA,
                    APPLICATION_REJECTED_BY_MA,
                    APPLICATION_APPROVED_BY_AEW,
                    APPLICATION_REJECTED_BY_AEW
                    -> true;
            default -> false;
        };
    }
}
