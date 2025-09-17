package com.hashjosh.document.mapper;

import com.hashjosh.document.config.CustomUserDetails;
import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.properties.MinioProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentMapper {
    
    private final MinioProperties minioProperties;
    
    public DocumentResponse toDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getId())
                .referenceId(document.getReferenceId())
                .uploadedBy(document.getUploadedBy())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .objectKey(document.getObjectKey())
                .uploadedAt(document.getUploadedAt())
                .metaData(document.getMetaData())
                .downloadUrl(generateDownloadUrl(document.getObjectKey()))
                .build();
    }

    public Document toDocument(String objectKey, CustomUserDetails userDetails, DocumentRequest request) {
        try {
            return Document.builder()
                    .fileName(request.file().getOriginalFilename())
                    .fileType(request.file().getContentType())
                    .referenceId(request.referenceId())
                    .uploadedBy(UUID.fromString(userDetails.getUserId()))
                    .objectKey(objectKey)
                    .metaData(request.metaData())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating document", e);
        }
    }
    
    private String generateDownloadUrl(String objectKey) {
        return String.format("%s/%s/%s", 
            minioProperties.url(), 
            minioProperties.bucket(), 
            objectKey);
    }
}
