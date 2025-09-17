package com.hashjosh.document.controller;

import com.hashjosh.document.dto.DocumentRequest;
import com.hashjosh.document.dto.DocumentResponse;
import com.hashjosh.document.service.DocumentService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
//    @PreAuthorize("hasAnyRole('ADMIN', 'FARMER')")
    public ResponseEntity<DocumentResponse> uploadDocumentAsync(@ModelAttribute DocumentRequest request)
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        DocumentResponse document = documentService.upload(request);
        return ResponseEntity.accepted().body(document);
    }
    @GetMapping("/{documentId}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable UUID documentId,
            HttpServletRequest request) 
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        var document = documentService.download(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.fileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, document.getContentDisposition())
                .body(document.fileData());
    }
    
    @GetMapping("/info/{documentId}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID documentId) {
        DocumentResponse document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/by-application/{applicationId}")
//    @PreAuthorize("isAuthenticated()")
    public List<DocumentResponse> getDocumentsByApplication(@PathVariable UUID applicationId) {
        return documentService.findByApplicationId(applicationId);
    }

    @GetMapping("/by-user/{userId}")
//    @PreAuthorize("isAuthenticated()")
    public List<DocumentResponse> getDocumentsByUser(@PathVariable UUID userId) {
        return documentService.findByUploadedBy(userId);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        return documentService.findAll(pageable);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAnyRole('ADMIN', 'FARMER')")
    public void deleteDocument(@PathVariable UUID documentId) 
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        documentService.delete(documentId);
    }

    @GetMapping("/{documentId}/metadata")
//    @PreAuthorize("isAuthenticated()")
    public DocumentResponse getDocumentMetadata(@PathVariable UUID documentId) {
        return documentService.getDocumentMetadata(documentId);
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
