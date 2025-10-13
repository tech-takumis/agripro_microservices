package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;

    public List<MessageDto> getAllMessagesWithAgricultureStaff(UUID farmerId) {
        List<Message> messages = messageRepository.findMessagesBetweenFarmerAndAgricultureStaff(farmerId);

        return messages.stream()
                .map(this::toMessageDto)
                .toList();
    }

    private MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .text(message.getText())
                .attachments(message.getAttachments().stream()
                        .map(this::getAttachmentUrl).collect(Collectors.toSet()))
                .build();
    }

    private String getAttachmentUrl(Attachment attachment) {
        return attachment.getUrl();
    }
}
