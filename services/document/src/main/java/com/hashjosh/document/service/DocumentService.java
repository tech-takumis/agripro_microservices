package com.hashjosh.document.service;

import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.exception.DocumentNotFoundException;
import com.hashjosh.document.mapper.DocumentMapper;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.properties.MinioProperties;
import com.hashjosh.document.repository.DocumentRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequestMapping
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;


    public DocumentResponse upload(DocumentRequest document)
            throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        String objectKey = UUID.randomUUID().toString() + "-" + document.file().getOriginalFilename();

        //Upload to minio
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .stream(document.file().getInputStream(), document.file().getSize(),-1)
                        .contentType(document.file().getContentType())
                        .build()
        );

        // Save metadata
        Document metaData = documentMapper.toDocument(objectKey,document);
        Document saved = documentRepository.save(metaData);
        return  documentMapper.toDocumentResponse(saved);
    }

    public byte[] download(UUID documentId, HttpServletRequest request) throws ServerException,
            InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document id "+documentId+ " not found!",
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI()

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
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI()
                ));

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(document.getObjectKey())
                        .build()
        );

        documentRepository.delete(document);

    }

    public String generatePresignedUrl(UUID documentId, int expiryMinutes) throws ServerException,
            InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioProperties.bucket())
                        .object(doc.getObjectKey())
                        .expiry(expiryMinutes, TimeUnit.MINUTES)
                        .build()
        );
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
