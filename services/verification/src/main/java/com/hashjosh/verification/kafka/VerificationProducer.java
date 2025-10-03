package com.hashjosh.verification.kafka;

import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import com.hashjosh.kafkacommon.application.ApplicationVerificationContract;
import com.hashjosh.verification.controller.VerificationController;
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
public class VerificationProducer {
    private final KafkaTemplate<String, ApplicationDomainEvent> kafkaTemplate;

    public  void publishEvent(String topic,ApplicationDomainEvent event) {
        log.info("Sending verification confirmation:: {}", event);

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build())
                .whenComplete((recordMetadata, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully to topic {} with offset {}", recordMetadata.getRecordMetadata().topic(), recordMetadata.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message: {}", ex.getMessage());
                    }
                });
    }

}
