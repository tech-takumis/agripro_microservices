package com.hashjosh.verification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public <T> void publishEvent(String topic, T event) {
        log.info("Publishing event: {}", event);

        kafkaTemplate.send(
                MessageBuilder
                .withPayload(event)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .build()).whenComplete(
                (result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", event, ex);
                    } else {
                        log.info("Event published successfully: {}", event);
                    }
                }
        );

        log.info("Publish request sent for event: {}", event);
    }
}
