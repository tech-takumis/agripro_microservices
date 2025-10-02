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

    private final KafkaTemplate<String, AgricultureRegistrationContract> template;

    public void publishAgricultureRegistrationEvent(AgricultureRegistrationContract agricultureRegistrationContract){
        Message<AgricultureRegistrationContract> contract = MessageBuilder
                .withPayload(agricultureRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "agriculture-event")
                .build();

        template.send(contract);
    }

}
