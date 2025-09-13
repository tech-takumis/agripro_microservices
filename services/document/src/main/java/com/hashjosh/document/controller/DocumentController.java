package com.hashjosh.document.controller;

import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.model.Document;
import com.hashjosh.document.service.DocumentService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> createDocument(
            @ModelAttribute DocumentRequest request

    ) throws ServerException,
            InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.upload(request));
    };

    @GetMapping("/{document-id}")
    public ResponseEntity<byte[]> download(
            @PathVariable("document-id")UUID documentId,
            HttpServletRequest request
            ) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        byte[] data = documentService.download(documentId,request);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+documentId)
                .body(data);
    }

    // Generate pre-signed url
    @GetMapping("/{id}/download-url")
    public ResponseEntity<String> getDownloadUrl(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "5") int expiryMinutes) throws Exception {
        String url = documentService.generatePresignedUrl(id, expiryMinutes);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload-url")
    public ResponseEntity<String> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "5") int expiryMinutes) throws Exception {
        String url = documentService.generatePresignedUploadUrl(fileName, expiryMinutes);
        return ResponseEntity.ok(url);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/document-id")
    public ResponseEntity<Void> delete(
            @PathVariable("document-id") UUID documentId,
            HttpServletRequest request
    ) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        documentService.delete(documentId,request);
        return ResponseEntity.noContent().build();
    }
}
