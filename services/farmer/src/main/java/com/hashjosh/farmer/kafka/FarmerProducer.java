package com.hashjosh.farmer.kafka;

import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FarmerProducer {

    private final KafkaTemplate<String, FarmerRegistrationContract> template;

    public void publishFarmerRegistrationEvent(FarmerRegistrationContract agricultureRegistrationContract){
        Message<FarmerRegistrationContract> contract = MessageBuilder
                .withPayload(agricultureRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "farmer-events")
                .build();

        template.send(contract);
    }

}
