package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Conversation;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.kafka.CommunicationPublisher;
import com.hashjosh.communication.repository.AttachmentRepository;
import com.hashjosh.communication.repository.ConversationRepository;
import com.hashjosh.communication.repository.MessageRepository;
import com.hashjosh.kafkacommon.communication.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommunicationPublisher publisher;
    private final ConversationRepository conversationRepository;

    @Transactional
    public Message saveMessage(MessageDto dto) {
        // Find existing conversation or create new one
        Conversation conversation = conversationRepository.findBySenderIdAndReceiverId(dto.getSenderId(), dto.getReceiverId())
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .senderId(dto.getSenderId())
                            .receiverId(dto.getReceiverId())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return conversationRepository.save(newConversation);
                });

        // Create message with initial attributes
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .text(dto.getText())
                .attachments(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        // Handle attachments
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            Set<Attachment> attachments = dto.getAttachments().stream()
                    .map(url -> attachmentRepository.findByUrl(url)
                            .orElseGet(() -> {
                                Attachment newAttachment = Attachment.builder()
                                        .url(url)
                                        .messages(new HashSet<>())
                                        .build();
                                return attachmentRepository.save(newAttachment);
                            }))
                    .collect(Collectors.toSet());

            // Add all attachments to the message
            attachments.forEach(message::addAttachment);
        }

        return messageRepository.save(message);
    }

    public void publishNewMessageEvent(Message message) {
        // Convert Set<Attachment> to List<String> for the event
        List<String> attachmentUrls = message.getAttachments().stream()
                .map(Attachment::getUrl)
                .collect(Collectors.toList());

        NewMessageEvent event = new NewMessageEvent(
                message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getText(),
                attachmentUrls,
                message.getCreatedAt()
        );
        publisher.publishEvent("new-message", event);
    }
}
