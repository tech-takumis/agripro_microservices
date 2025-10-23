package com.hashjosh.realtimegatewayservice.kafka;

import com.hashjosh.kafkacommon.communication.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageConsumer {
    private final  SimpUserRegistry simpUserRegistry;

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "messages-lifecycle", groupId = "realtime-gateway-group")
    public void consume(@Payload NewMessageEvent event){
        try {
            log.info("Current websocket users:");
            simpUserRegistry.getUsers().forEach(u -> log.info(" - {}", u.getName()));
            log.info("📥 Consumed message event for receiverId={} senderId={}", event.getReceiverId(), event.getSenderId());

            // Send to receiver
            messagingTemplate.convertAndSendToUser(
                    event.getReceiverId().toString(),
                    "/queue/private.messages",
                    event
            );

            // Echo to sender
            messagingTemplate.convertAndSendToUser(
                    event.getSenderId().toString(),
                    "/queue/private.messages",
                    event
            );

            log.info("✅ Broadcasted message {} → {}", event.getSenderId(), event.getReceiverId());

        } catch (Exception e) {
            log.error("🚫 Failed to broadcast WebSocket message: {}", e.getMessage(), e);
        }
    }
}
