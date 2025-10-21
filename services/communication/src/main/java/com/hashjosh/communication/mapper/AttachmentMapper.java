package com.hashjosh.communication.mapper;


import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.constant.communication.AttachmentResponseDto;
import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.kafkacommon.communication.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttachmentMapper {
    private final DocumentClient documentClient;
    public AttachmentResponseDto toAttachmentResponseDto(Attachment attachment) {
        return AttachmentResponseDto.builder()
                .documentId(attachment.getDocumentId())
                .url(documentClient.getDocumentPreviewUrl(attachment.getDocumentId()))
                .build();
    }

    public Attachment toAttachmentEntity(DocumentResponse documentResponse, Message message) {
        return Attachment.builder()
                .message(message)
                .documentId(documentResponse.getDocumentId())
                .build();
    }
}
