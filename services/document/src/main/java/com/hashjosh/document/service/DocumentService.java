package com.hashjosh.document.service;

import com.hashjosh.document.config.CustomUserDetails;
import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.exception.DocumentNotFoundException;
import com.hashjosh.document.mapper.DocumentMapper;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.exception.FileValidationException;
import com.hashjosh.document.properties.MinioProperties;
import com.hashjosh.document.repository.DocumentRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileValidationService fileValidationService;

    @Transactional
    public DocumentResponse upload(DocumentRequest request)
            throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException, FileValidationException {
        
        // Validate the file before processing
        fileValidationService.validateFile(request.file());
        
        // Generate a unique object key
        String originalFilename = request.file().getOriginalFilename();
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
                .stream(request.file().getInputStream(), request.file().getSize(), -1)
                .contentType(request.file().getContentType())
                .build()
        );

        // Save document metadata to database
        Document document = documentMapper.toDocument(objectKey,userDetails, request);
        Document savedDocument = documentRepository.save(document);
        
        return documentMapper.toDocumentResponse(savedDocument);
    }

    public DocumentResponse.DownloadFile download(UUID documentId) 
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                        ));

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(document.getObjectKey())
                        .build())) {
            
            byte[] fileData = stream.readAllBytes();
            
            return new DocumentResponse.DownloadFile(
                    document.getFileName(),
                    document.getFileType(),
                    fileData
            );
        }
    }

    @Transactional
    public void delete(UUID documentId) 
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                        ));
        
        // Delete from MinIO
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(minioProperties.bucket())
                .object(document.getObjectKey())
                .build()
        );
        
        // Delete from database
        documentRepository.delete(document);
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
    public List<DocumentResponse> findByApplicationId(UUID applicationId) {
        return documentRepository.findByReferenceId(applicationId)
                .stream()
                .map(documentMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> findByUploadedBy(UUID userId) {
        return documentRepository.findByUploadedBy(userId).stream()
                .map(documentMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    public Page<DocumentResponse> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable)
                .map(documentMapper::toDocumentResponse);
    }

    public DocumentResponse getDocumentMetadata(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                        ));
        return documentMapper.toDocumentResponse(document);
    }

    public String generatePresignedUrl(UUID documentId, int expiryMinutes) 
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
                
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found with id: " + documentId,
                        HttpStatus.NOT_FOUND.value()
                        ));
                
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioProperties.bucket())
                .object(document.getObjectKey())
                .expiry(expiryMinutes, TimeUnit.MINUTES)
                .build()
        );
    }


    public byte[] download(UUID documentId, HttpServletRequest request) throws ServerException,
            InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document id "+documentId+ " not found!",
                        HttpStatus.NOT_FOUND.value()
                ));

        // Download the document
        try(InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(document.getObjectKey())
                        .build()
        )){
            return inputStream.readAllBytes();
        }
    }

    public void delete(UUID documentId, HttpServletRequest request) throws ServerException,
            InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
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

    }


    public String generatePresignedUploadUrl(String fileName, int expiryMinutes)
            throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        String objectKey = UUID.randomUUID() + "-" + fileName;

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .expiry(expiryMinutes, TimeUnit.MINUTES)
                        .build()
        );
    }

}
