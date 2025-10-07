package com.hashjosh.document.mapper;

import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.document.config.CustomUserDetails;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.properties.MinioProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentMapper {
    
    private final MinioProperties minioProperties;
    
    public DocumentResponse toDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .documentId(document.getId())
                .uploadedBy(document.getUploadedBy())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .objectKey(document.getObjectKey())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    public Document toDocument(String objectKey, CustomUserDetails userDetails, MultipartFile file) {
        try {
            return Document.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .uploadedBy(UUID.fromString(userDetails.getUserId()))
                    .objectKey(objectKey)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating document", e);
        }
    }
}
