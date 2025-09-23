package com.hashjosh.application.kafka;

import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationProducer {

    private final KafkaTemplate<String, ApplicationSubmissionContract> kafkaTemplate;

    public void submitApplication(ApplicationSubmissionContract applicationSubmissionContract) {
        log.info("submitting application::: {}", applicationSubmissionContract);

        Message<ApplicationSubmissionContract> message = MessageBuilder
                .withPayload(applicationSubmissionContract)
                .setHeader(KafkaHeaders.TOPIC, "application-events")
                .build();

        kafkaTemplate.send(message);
    }
}
