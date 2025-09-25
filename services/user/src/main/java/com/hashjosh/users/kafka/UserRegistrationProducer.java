package com.hashjosh.users.kafka;

import com.hashjosh.kafkacommon.user.UserRegistrationContract;
import com.hashjosh.users.dto.UserRegistrationRequest;
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
public class UserRegistrationProducer {

    private final KafkaTemplate<String, UserRegistrationRequest> kafkaTemplate;

    public void userRegistration(UserRegistrationContract userRegistrationContract){
        log.info("User registration payload: {}", userRegistrationContract);

        Message<UserRegistrationContract> message = MessageBuilder
                .withPayload(userRegistrationContract)
                .setHeader(KafkaHeaders.TOPIC, "user-event")
                .build();

        kafkaTemplate.send(message);
        log.info("User registration sent to kafka");
    }
}
