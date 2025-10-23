package com.hashjosh.communication.service;

import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.config.CustomUserDetails;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.kafka.CommunicationPublisher;
import com.hashjosh.communication.mapper.AttachmentMapper;
import com.hashjosh.communication.mapper.MessageMapper;
import com.hashjosh.communication.repository.AttachmentRepository;
import com.hashjosh.communication.repository.MessageRepository;
import com.hashjosh.constant.communication.AttachmentResponseDto;
import com.hashjosh.constant.communication.MessageRequestDto;
import com.hashjosh.constant.communication.MessageResponseDto;
import com.hashjosh.constant.communication.enums.ConversationType;
import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.kafkacommon.communication.AttachmentResponse;
import com.hashjosh.kafkacommon.communication.NewMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final AttachmentMapper attachmentMapper;
    private final CommunicationPublisher publisher;
    private final DocumentClient documentClient;
    private final AttachmentRepository attachmentRepository;


    public List<MessageResponseDto> getAllMessagesWithAgricultureStaff(UUID farmerId) {
        List<Message> messages = messageRepository.findMessagesByFarmerIdAndConversationType(farmerId);

        return messages.stream()
                .map(messageMapper::toMessageResponseDto)
                .toList();
    }


    @Transactional
    public MessageResponseDto createMessage(MessageRequestDto messageRequestDto, List<MultipartFile> attachments) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        messageRequestDto.setSenderId(UUID.fromString(userDetails.getUserId()));

        // Save message
        Message message =  messageRepository.save(
                messageMapper.toMessageEntity(messageRequestDto)
        );

        // Persist attachments and associate with message
        Set<AttachmentResponseDto> attachmentResponses = new HashSet<>();
        if(attachments != null && !attachments.isEmpty()) {
            List<Attachment> persistedAttachments = new ArrayList<>();
            for (MultipartFile file : attachments) {
                DocumentResponse documentResponse = documentClient
                        .uploadDocument(file,userDetails.getUserId());

                Attachment attachment = attachmentMapper.toAttachmentEntity(
                        documentResponse,
                        message
                );
                // Persist attachment
                attachment = attachmentRepository.save(attachment);
                persistedAttachments.add(attachment);

                AttachmentResponseDto attachmentResponse = attachmentMapper.toAttachmentResponseDto(attachment);
                attachmentResponses.add(attachmentResponse);
            }
            // Set attachments to message entity
            message.setAttachments(persistedAttachments);
            // Save message again to update attachments relationship
            messageRepository.save(message);
        }

        // Map to response DTO
        MessageResponseDto responseDto = messageMapper.toMessageResponseDto(message);
        responseDto.setAttachments(attachmentResponses);

        NewMessageEvent messageEvent = NewMessageEvent.builder()
                .messageId(responseDto.getMessageId())
                .conversationType(messageRequestDto.getType())
                .senderId(messageRequestDto.getSenderId())
                .receiverId(messageRequestDto.getReceiverId())
                .text(responseDto.getText())
                .attachmentResponses(responseDto.getAttachments().stream().map(attachment ->
                        AttachmentResponse.builder()
                                .attachmentId(attachment.getAttachmentId())
                                .documentId(attachment.getDocumentId())
                                .url(attachment.getUrl())
                                .build()
                ).toList())
                .timestamp(responseDto.getSentAt())
                .build();

        publisher.publishEvent("messages-lifecycle", messageEvent);

        return responseDto;
    }
}
