package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    /**
     * Handles public messages sent to a conversation topic.
     * @param messageDto The message payload.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDto messageDto){
        Message savedMessage = messageService.saveMessage(messageDto);
        // Broadcast to the public conversation topic
        messagingTemplate.convertAndSend("/topic/conversation/" + messageDto.getConversationId(), messageDto);
        messageService.publishNewMessageEvent(savedMessage);
    }

    /**
     * Handles private messages sent to a specific user.
     * Assumes messageDto contains the recipientId (username or user ID).
     * @param messageDto The message payload.
     */
    @MessageMapping("/private.chat")
    public void sendPrivateMessage(@Payload MessageDto messageDto) {
        Message savedMessage = messageService.saveMessage(messageDto);
        // Send message to the specific user's private message topic
        // This resolves to /user/{recipientId}/topic/private
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDto.getRecipientId()), "/topic/private", messageDto
        );
        messageService.publishNewMessageEvent(savedMessage);
    }

    /**
     * Handles notifications sent to a specific user.
     * Assumes messageDto contains the recipientId (username or user ID).
     * @param notificationDto The notification payload.
     */
    @MessageMapping("/notification")
    public void sendNotification(@Payload MessageDto notificationDto) {
        Message savedNotification = messageService.saveMessage(notificationDto);
        // Send notification to the specific user's notification topic
        // This resolves to /user/{recipientId}/topic/notification
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notificationDto.getRecipientId()), "/topic/notification", notificationDto
        );
        // You might want a dedicated Kafka publisher for notifications
        messageService.publishNewMessageEvent(savedNotification);
    }
}
