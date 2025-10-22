package com.hashjosh.communication.mapper;

import com.hashjosh.constant.communication.MessageRequestDto;
import com.hashjosh.constant.communication.MessageResponseDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.constant.communication.AttachmentResponseDto;
import com.hashjosh.kafkacommon.communication.AttachmentResponse;
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

    public Message toMessageEntity(MessageRequestDto messageRequestDto) {
        return Message.builder()
                .receiverId(messageRequestDto.getReceiverId())
                .senderId(messageRequestDto.getSenderId())
                .type(messageRequestDto.getType())
                .text(messageRequestDto.getText())
                .build();
    }
}
