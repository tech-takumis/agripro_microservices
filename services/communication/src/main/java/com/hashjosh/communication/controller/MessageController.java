package com.hashjosh.communication.controller;

import com.hashjosh.communication.config.CustomUserDetails;
import com.hashjosh.communication.dto.MessageRequestDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.repository.DesignatedStaffRepository;
import com.hashjosh.communication.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final DesignatedStaffRepository designatedStaffRepository;

    /**
     * Handles public messages sent to a conversation topic.
     * @param dto The message payload.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequestDto dto,Principal principal){
        log.info("Sending private message to user ID: {}", dto.getReceiverId());
        log.info("Message content: {}", dto.getText());

        log.info("Principal class: {}", principal != null ? principal.getClass() : "null");
        log.info("Principal value: {}", principal);

        UUID userId;
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            CustomUserDetails userDetails = (CustomUserDetails) token.getPrincipal();
            userId = UUID.fromString(userDetails.getUserId());
        } else {
            throw new RuntimeException("No authenticated principal found");
        }
        Message savedMessage = messageService.saveMessage(dto,userId);
        // Broadcast to the public conversation topic
        messagingTemplate.convertAndSend("/topic/conversation/" + savedMessage.getConversationId(), dto);
        messageService.publishNewMessageEvent(savedMessage);
    }

    /**
     * Handles private messages sent to a specific user.
     * Assumes messageDto contains the recipientId (username or user ID).
     * @param dto The message payload.
     */
    @MessageMapping("/private.chat")
    public void sendPrivateMessage(@Payload MessageRequestDto dto, Principal principal) {
        log.info("Sending private message to user ID: {}", dto.getReceiverId());
        log.info("Message content: {}", dto.getText());

        log.info("Principal class: {}", principal != null ? principal.getClass() : "null");
        log.info("Principal value: {}", principal);

        UUID userId;
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            CustomUserDetails userDetails = (CustomUserDetails) token.getPrincipal();
             userId = UUID.fromString(userDetails.getUserId());
        } else {
            throw new RuntimeException("No authenticated principal found");
        }

        Message savedMessage = messageService.saveMessage(dto,userId);
        // Send message to the specific user's private message topic
        // This resolves to /user/{recipientId}/topic/private
        messagingTemplate.convertAndSendToUser(
                String.valueOf(dto.getReceiverId()), "/topic/private", dto
        );
        messageService.publishNewMessageEvent(savedMessage);
    }


//    @MessageMapping("/notification")
//    public void sendNotification(@Payload MessageRequestDto notificationDto) {
//        Message savedNotification = messageService.saveMessage(notificationDto);
//        // Send notification to the specific user's notification topic
//        // This resolves to /user/{recipientId}/topic/notification
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(notificationDto.getReceiverId()), "/topic/notification", notificationDto
//        );
//        // You might want a dedicated Kafka publisher for notifications
//        messageService.publishNewMessageEvent(savedNotification);
//    }
}
