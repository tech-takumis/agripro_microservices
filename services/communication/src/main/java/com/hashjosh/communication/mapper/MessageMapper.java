package com.hashjosh.communication.mapper;

import com.hashjosh.communication.dto.MessageResponseDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.dto.AttachmentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageMapper {

    private final AttachmentMapper attachmentMapper;

    public MessageResponseDto toMessageResponseDto(Message message) {
        Set<AttachmentResponseDto> attachmentResponse = message.getAttachments().stream()
                .map(attachmentMapper::toAttachmentResponseDto )
                .collect(Collectors.toSet());

        return MessageResponseDto .builder()
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .attachments(attachmentResponse)
                .text(message.getText())
                .sentAt(message.getCreatedAt())
                .build();
    }

}
