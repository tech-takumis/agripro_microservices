package com.hashjosh.communication.controller;

import com.hashjosh.communication.config.CustomUserDetails;
import com.hashjosh.communication.dto.AttachmentResponseDto;
import com.hashjosh.communication.dto.MessageRequestDto;
import com.hashjosh.communication.dto.MessageResponseDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.exception.InvalidJwtException;
import com.hashjosh.communication.service.MessageService;
import com.hashjosh.communication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    /**
     * Handles public messages sent to a conversation topic.
     * @param dto The message payload.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequestDto dto, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Sending message to conversation");
        log.info("Message content: {}", dto.getText());

        CustomUserDetails userDetails = extractUserDetails(headerAccessor);
        UUID userId = UUID.fromString(userDetails.getUserId());

        Message savedMessage = messageService.saveMessage(dto, userId);

        MessageResponseDto response = MessageResponseDto.builder()
                .messageId(savedMessage.getId())
                .senderId(userId)
                .text(dto.getText())
                .type(dto.getType())
                .attachments(savedMessage.getAttachments().stream()
                        .map(attachment -> AttachmentResponseDto.builder()
                                .attachmentId(attachment.getId())
                                .documentId(attachment.getDocumentId())
                                .build())
                        .collect(Collectors.toSet()))
                .sentAt(savedMessage.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/conversation/" + savedMessage.getConversationId(), response);
        messageService.publishNewMessageEvent(savedMessage);
    }

    /**
     * Handles private messages sent to a specific user.
     * Assumes messageDto contains the recipientId (username or user ID).
     * @param dto The message payload.
     */
    // ---------------------------------------------------------------------
    // ✅ Private message endpoint using convertAndSendToUser
    // ---------------------------------------------------------------------
    @MessageMapping("/private.chat")
    public void sendPrivateMessage(@Payload MessageRequestDto dto, SimpMessageHeaderAccessor headerAccessor) {
        UUID senderId = dto.getSenderId();
        UUID receiverId = dto.getReceiverId();

        if (senderId == null || receiverId == null) {
            throw new InvalidJwtException("Sender or receiver ID missing");
        }

        // Validate users exist
        if (!userService.userExists(senderId) || !userService.userExists(receiverId)) {
            log.error("❌ Invalid sender or receiver: sender={}, receiver={}", senderId, receiverId);
            throw new InvalidJwtException("Invalid sender or receiver");
        }

        // Normalize fields
        if (dto.getText() == null) dto.setText("");
        if (dto.getSentAt() == null) dto.setSentAt(LocalDateTime.now());

        // Save message in DB
        Message savedMessage = messageService.saveMessage(dto, senderId);

        // Build response payload
        MessageResponseDto response = MessageResponseDto.builder()
                .messageId(savedMessage.getId())
                .senderId(senderId)
                .receiverId(receiverId)
                .text(dto.getText())
                .type(dto.getType())
                .attachments(savedMessage.getAttachments().stream()
                        .map(att -> AttachmentResponseDto.builder()
                                .attachmentId(att.getId())
                                .documentId(att.getDocumentId())
                                .build())
                        .collect(Collectors.toSet()))
                .sentAt(savedMessage.getCreatedAt())
                .build();

        // -----------------------------------------------------------------
        // ✅ Send message via user destination
        // -----------------------------------------------------------------
        try {
            // send to receiver
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/private.messages",
                    response
            );

            // optional: send echo to sender (so they also see their sent message)
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/private.messages",
                    response
            );

            log.info("✅ Sent private message from {} → {}", senderId, receiverId);

        } catch (Exception e) {
            log.error("🚫 Failed to send message to user: {}", e.getMessage(), e);
        }

        // Publish for Kafka or async listeners
        messageService.publishNewMessageEvent(savedMessage);
    }

    private CustomUserDetails extractUserDetails(SimpMessageHeaderAccessor headerAccessor) {
        log.debug("🔍 Extracting user details from message headers: {}", headerAccessor.getMessageHeaders());

        // First try to get from accessor's User
        if (headerAccessor.getUser() instanceof CustomUserDetails customUserDetails) {
            log.debug("✅ Found CustomUserDetails in accessor's User: {}", customUserDetails.getUsername());
            return customUserDetails;
        }

        // Then try security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            log.debug("✅ Found CustomUserDetails in SecurityContext: {}", customUserDetails.getUsername());
            return customUserDetails;
        }


        // If we get here, we couldn't find valid authentication
        log.error("❌ No valid authentication found in message headers or security context");
        throw new InvalidJwtException("No authenticated user found");
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
