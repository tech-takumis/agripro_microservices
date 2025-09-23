package com.hashjosh.verification.kafka;

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
    private final KafkaTemplate<String, VerificationController> kafkaTemplate;


    public void submitVerifiedApplication(ApplicationVerificationContract applicationVerificationContract) {
        log.info("Sending verification confirmation:: {}", applicationVerificationContract);

        Message<ApplicationVerificationContract> message =
                MessageBuilder.withPayload(applicationVerificationContract)
                        .setHeader(KafkaHeaders.TOPIC,"application-events")
                        .build();

        kafkaTemplate.send(message);
    }

}
