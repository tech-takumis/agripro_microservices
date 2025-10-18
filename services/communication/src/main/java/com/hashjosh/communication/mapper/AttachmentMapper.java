package com.hashjosh.communication.mapper;


import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.entity.Attachment;
import com.hashjosh.communication.dto.AttachmentResponseDto;
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
}
