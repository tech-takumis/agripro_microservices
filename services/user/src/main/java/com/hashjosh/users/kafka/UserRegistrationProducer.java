package com.hashjosh.users.kafka;

import com.hashjosh.kafkacommon.user.FarmerRegistrationContract;
import com.hashjosh.kafkacommon.user.StaffRegistrationContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationProducer {

    private final KafkaTemplate<String, StaffRegistrationContract> kafkaTemplate;

    public void staffRegistrationEvent(StaffRegistrationContract staffRegistrationContract){
        log.info("User registration payload: {}", staffRegistrationContract);

        Message<StaffRegistrationContract> message = MessageBuilder
                .withPayload(staffRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "staff-event")
                .build();

        kafkaTemplate.send(message);
        log.info("User registration sent to kafka");
    }

    public void farmerRegistrationEvent(FarmerRegistrationContract farmerRegistrationContract){
        log.info("Farmer registration payload: {}", farmerRegistrationContract);

        Message<FarmerRegistrationContract> message = MessageBuilder
                .withPayload(farmerRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "farmer-event")
                .build();

        kafkaTemplate.send(message);
        log.info("Farmer registration sent to kafka");
    }
}
