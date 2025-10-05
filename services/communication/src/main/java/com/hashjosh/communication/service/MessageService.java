package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Conversation;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.entity.User;
import com.hashjosh.communication.kafka.CommunicationPublisher;
import com.hashjosh.communication.mapper.MessageMapper;
import com.hashjosh.communication.repository.ConversationRepository;
import com.hashjosh.communication.repository.MessageRepository;
import com.hashjosh.kafkacommon.communication.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final CommunicationPublisher publisher;
    private final ConversationRepository conversationRepository;

    public Message saveMessage(MessageDto dto) {
        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new IllegalStateException("Conversation not found"));

        User sender = conversation.getSender();
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .text(dto.getText())
                .attachments(dto.getAttachments())
                .createdAt(LocalDateTime.now())
                .build();
        return messageRepository.save(message);
    }

    public void publishNewMessageEvent(Message message) {
        NewMessageEvent event = new NewMessageEvent(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getText(),
                message.getAttachments(),
                message.getCreatedAt()
        );
        publisher.publishEvent("new-message",event);
    }
}
