package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.AgricultureMessageRequestDto;
import com.hashjosh.communication.dto.MessageRequestDto;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Conversation;
import com.hashjosh.communication.entity.DesignatedStaff;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.kafka.CommunicationPublisher;
import com.hashjosh.communication.mapper.AttachmentMapper;
import com.hashjosh.communication.repository.AttachmentRepository;
import com.hashjosh.communication.repository.ConversationRepository;
import com.hashjosh.communication.repository.MessageRepository;
import com.hashjosh.constant.communication.enums.ConversationType;
import com.hashjosh.kafkacommon.communication.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommunicationPublisher publisher;
    private final ConversationRepository conversationRepository;
    private final AttachmentMapper attachmentMapper;

    @Transactional
    public Message saveMessage(MessageRequestDto dto, UUID senderId) {
        // Find existing conversation or create new one
        Conversation conversation = conversationRepository.findBySenderIdAndReceiverId(senderId, dto.getReceiverId())
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .senderId(senderId)
                            .receiverId(dto.getReceiverId())
                            .createdAt(LocalDateTime.now())
                            .type(ConversationType.valueOf(dto.getType()))
                            .build();
                    return conversationRepository.save(newConversation);
                });

        // Save the message first, without attachments
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(senderId)
                .receiverId(dto.getReceiverId())
                .text(dto.getText())
                .attachments(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message); // Persist the message

        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            Set<Attachment> attachments = dto.getAttachments().stream()
                    .map(attachmentRequest -> attachmentRepository.findByDocumentId(attachmentRequest.getDocumentId())
                            .orElseGet(() -> {
                                Attachment newAttachment = Attachment.builder()
                                        .documentId(attachmentRequest.getDocumentId())
                                        .message(savedMessage)
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
        List<UUID> attachmentUrls = message.getAttachments().stream()
                .map(Attachment::getDocumentId)
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

    public Message saveAgricultureMessage(AgricultureMessageRequestDto dto, DesignatedStaff staff) {

        Conversation conversation = conversationRepository.findBySenderIdAndReceiverId(dto.getSenderId(), staff.getUserId())
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .senderId(dto.getSenderId())
                            .receiverId(staff.getUserId())
                            .createdAt(LocalDateTime.now())
                            .type(ConversationType.FARMER_AGRICULTURE)
                            .build();
                    return conversationRepository.save(newConversation);
                });


        // Save the message first, without attachments
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(dto.getSenderId())
                .receiverId(staff.getUserId())
                .text(dto.getText())
                .attachments(new HashSet<>())
                .createdAt(dto.getSendAt())
                .build();

        Message savedMessage = messageRepository.save(message); // Persist the message

        if(dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            Set<Attachment> attachments = dto.getAttachments().stream()
                    .map(attachmentRequest -> attachmentRepository.findByDocumentId(attachmentRequest.getDocumentId())
                            .orElseGet(() -> {
                                Attachment newAttachment = Attachment.builder()
                                        .documentId(attachmentRequest.getDocumentId())
                                        .message(savedMessage)
                                        .build();
                                return attachmentRepository.save(newAttachment);
                            }))
                    .collect(Collectors.toSet());

            // Add all attachments to the message
            savedMessage.setAttachments(attachments);
        }

        return messageRepository.save(savedMessage);

    }
}
