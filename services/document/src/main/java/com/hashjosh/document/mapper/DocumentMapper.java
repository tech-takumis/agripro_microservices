package com.hashjosh.document.mapper;

import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.document.config.CustomUserDetails;
import com.hashjosh.document.exception.ApiException;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.properties.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentMapper {
    
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    
    public DocumentResponse toDocumentResponse(Document document) {
        try {
            return DocumentResponse.builder()
                    .documentId(document.getId())
                    .uploadedBy(document.getUploadedBy())
                    .fileName(document.getFileName())
                    .fileType(document.getFileType())
                    .objectKey(document.getObjectKey())
                    .preview(generatePresignedUrl(document.getObjectKey(), 60, Method.GET))
                    .uploadedAt(document.getUploadedAt())
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate document response", e);
            throw ApiException.internalError("Failed to generate document response");
        }
    }

    public Document toDocument(String objectKey, CustomUserDetails userDetails, MultipartFile file) {
        try {
            return Document.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .uploadedBy(UUID.fromString(userDetails.getUserId()))
                    .objectKey(objectKey)
                    .uploadedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating document", e);
        }
    }

    private String generatePresignedUrl(String objectKey, int expiryMin, Method method) {
        try {
            String presigned = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(minioProperties.bucket())
                            .object(objectKey)
                            .expiry(expiryMin, TimeUnit.MINUTES)
                            .build()
            );

            // Automatically rewrite internal to external url for client-safe access
            if(!minioProperties.urlInternal().equals(minioProperties.urlExternal())) {
                presigned = presigned.replace(
                        minioProperties.urlInternal(),
                        minioProperties.urlExternal()
                );
            }
            log.debug("Generated presigned URL: {}", presigned);
            return presigned;
        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            throw ApiException.internalError("Failed to generate presigned URL");
        }
    }
}
