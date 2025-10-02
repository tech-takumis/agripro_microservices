package com.hashjosh.pcic.kafka;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PcicProducer {

    private final KafkaTemplate<String, AgricultureRegistrationContract> template;

    public void publishPcicRegistrationEvent(AgricultureRegistrationContract agricultureRegistrationContract){
        Message<AgricultureRegistrationContract> contract = MessageBuilder
                .withPayload(agricultureRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "pcic-event")
                .build();

        template.send(contract);
    }

}
