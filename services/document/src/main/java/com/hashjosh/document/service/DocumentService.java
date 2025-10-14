package com.hashjosh.document.service;

import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.constant.document.dto.DownloadFile;
import com.hashjosh.document.config.CustomUserDetails;
import com.hashjosh.document.exception.DocumentNotFoundException;
import com.hashjosh.document.exception.FileValidationException;
import com.hashjosh.document.exception.MinioOperationException;
import com.hashjosh.document.mapper.DocumentMapper;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.properties.MinioProperties;
import com.hashjosh.document.repository.DocumentRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileValidationService fileValidationService;

    @Transactional
    public DocumentResponse upload(MultipartFile file)
            throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileValidationException {
        
        // Validate the file before processing
        fileValidationService.validateFile(file);
        
        // Generate a unique object key
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectKey = String.format("%s%s", UUID.randomUUID(), fileExtension);

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // Upload to MinIO
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioProperties.bucket())
                .object(objectKey)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );

        // Save document metadata to database
        Document document = documentMapper.toDocument(objectKey,userDetails, file);
        Document savedDocument = documentRepository.save(document);
        return  documentMapper.toDocumentResponse(savedDocument);
    }

    public DownloadFile download(UUID documentId) {
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                        ));

        return new DownloadFile(
                document.getFileName(),
                document.getFileType(),
                getFile(document.getObjectKey())
        );
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                ));
        return documentMapper.toDocumentResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findByUploadedBy(UUID userId) {
        return documentRepository.findByUploadedBy(userId).stream()
                .map(documentMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toDocumentResponse)
                .toList();
    }

    public void delete(UUID documentId) {
        try {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(
                            "Document id "+documentId+ " not found!",
                            HttpStatus.NOT_FOUND.value()
                    ));

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.bucket())
                            .object(document.getObjectKey())
                            .build()
            );

            documentRepository.delete(document);
        } catch (Exception e) {
            log.error("Failed to delete document", e);
            throw new MinioOperationException("Failed to delete document from storage",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    private byte[] getFile(String objectKey) {
        try(InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .build()
        )){
            return stream.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to get file from storage", e);
            throw new MinioOperationException(
                    "Failed to retrieve file from storage",
                    HttpStatus.NON_AUTHORITATIVE_INFORMATION.value(),
                    e
            );
        }
    }

    public String generatePresignedUrl(UUID documentId, Method method) {
        try {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(
                            "Document not found with id: " + documentId,
                            HttpStatus.NOT_FOUND.value()
                    ));

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(minioProperties.bucket())
                            .object(document.getObjectKey())
                            .expiry(30, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            throw new MinioOperationException(
                    "Failed to generate presigned URL",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e
            );
        }
    }
}
