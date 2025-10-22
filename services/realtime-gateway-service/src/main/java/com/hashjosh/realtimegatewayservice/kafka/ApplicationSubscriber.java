package com.hashjosh.realtimegatewayservice.kafka;

import com.hashjosh.kafkacommon.application.ApplicationReceivedEvent;
import com.hashjosh.realtimegatewayservice.template.NotificationTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group")
    public void consumeApplicationReceivedEvent(@Payload ApplicationReceivedEvent event) {
        log.info("Received Application Event: {}", event);
        // Process the received event as needed

        NotificationTemplate template = NotificationTemplate.builder()
                .title(event.getVerificationStatus())
                .message(event.getDetials())
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(String.valueOf(event.getUploadedBY()), "/queue/application.lifecycle", template);


    }

}
