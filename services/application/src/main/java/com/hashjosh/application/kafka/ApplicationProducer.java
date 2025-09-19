package com.hashjosh.application.kafka;

import com.hashjosh.kafkacommon.application.ApplicationContract;
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

    private final KafkaTemplate<String, ApplicationContract> kafkaTemplate;

    public void submitApplication(ApplicationContract applicationContract) {
        log.info("submitting application::: {}", applicationContract.getPayload());

        Message<ApplicationContract> message = MessageBuilder
                .withPayload(applicationContract)
                .setHeader(KafkaHeaders.TOPIC, "application-events")
                .build();

        kafkaTemplate.send(message);
    }
}
