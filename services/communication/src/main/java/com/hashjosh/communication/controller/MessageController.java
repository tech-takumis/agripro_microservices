package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.kafka.CommunicationPublisher;
import com.hashjosh.communication.mapper.MessageMapper;
import com.hashjosh.communication.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDto messageDto){

        // Save message to database
        Message saveMessage = messageService.saveMessage(messageDto);
        // Broadcast to conversation via websocket
        messagingTemplate.convertAndSend("/topic/conversation/"+messageDto.getConversationId(),messageDto);
        // Publish to kafka for other services
        messageService.publishNewMessageEvent(saveMessage);
    }

}
