package com.hashjosh.application.kafka;

import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void publishEvent(String topic,T event) {
        log.info("submitting application::: {}", event);

        kafkaTemplate.send(MessageBuilder
                        .withPayload(event)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .build())
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully to topic {} with offset {}", result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message: {}", ex.getMessage());
                    }
                });
    }
}
