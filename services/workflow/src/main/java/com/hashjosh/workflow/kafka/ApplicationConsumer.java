package com.hashjosh.workflow.kafka;

import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.workflow.utils.KafkaUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ApplicationConsumer {
    private final KafkaUtils kafkaUtils;

    @PostConstruct
    public void check() {
        log.info("✅ Kafka consumer config loaded, groupId=workflow-service");
    }


    @KafkaListener(topics = "application-events")
    public void consumeSubmittedApplication(ApplicationContract applicationContract) {
        try {
            log.info("Consume application submission event: {}", applicationContract);

            switch (applicationContract.getEventType()) {
                case "application-submitted", "application-approved-by-municipal-agriculturist",
                     "application-rejected", "application-rejected-by-municipal-agriculturist",
                     "application-under-review", "application-cancelled-by-municipal-agriculturist",
                     "application-claim-approved"->
                    kafkaUtils.handleApplicationEvent(applicationContract);
                default -> 
                    log.debug("Ignoring unknown event type {}", applicationContract.getEventType());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process application event: {}", applicationContract, e);
            throw e; // ✅ Let Spring Kafka handle retry + DLT
        }
    }



}
