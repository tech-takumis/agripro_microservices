package com.hashjosh.communication.service;

import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;
    private final DocumentClient documentClient;

    public List<MessageDto> getAllMessagesWithAgricultureStaff(UUID farmerId) {
        List<Message> messages = messageRepository.findMessagesByFarmerIdAndConversationType(farmerId);

        return messages.stream()
                .map(this::toMessageDto)
                .toList();
    }

    private MessageDto toMessageDto(Message message) {
        Set<String> attachmentUrl = getAttachmentUrls(message.getAttachments());

        return MessageDto.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .text(message.getText())
                .attachments(attachmentUrl)
                .sentAt(message.getCreatedAt())
                .build();
    }

    private Set<String> getAttachmentUrls(Set<Attachment> attachments) {
        return attachments.stream()
                .map(attachment -> documentClient.getDocumentPreviewUrl(attachment.getDocumentId()))
                .collect(Collectors.toSet());
    }
}
