package com.example.agriculture.kafka;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgricultureProducer {

    private final KafkaTemplate<String, Object> template;

    public <T> void publishEvent(String topic, T event){
        Message<T> contract = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        template.send(contract);
    }

}
