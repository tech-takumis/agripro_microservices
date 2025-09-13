package com.hashjosh.document.mapper;

import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    public DocumentResponse toDocumentResponse(Document saved) {
        return new DocumentResponse(
                saved.getId(),
                saved.getApplicationId(),
                saved.getUploadedBy(),
                saved.getFileName(),
                saved.getFileType(),
                saved.getObjectKey(),
                saved.getUploadedAt()
        );
    }

    public Document toDocument(String objectKey, DocumentRequest document) {
        return Document.builder()
                .fileName(document.file().getOriginalFilename())
                .fileType(document.file().getContentType())
                .applicationId(document.applicationId())
                .uploadedBy(document.uploadedBy())
                .objectKey(objectKey)
                .build();
    }
}
