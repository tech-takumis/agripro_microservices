package com.hashjosh.document.controller;

import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.document.exception.FileValidationException;
import com.hashjosh.document.service.DocumentService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestPart("file") MultipartFile file
    ) {
        try {

            DocumentResponse document = documentService.upload(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (FileValidationException e) {
            return ResponseEntity.badRequest()
                    .body(DocumentResponse.builder()
                            .fileName(file.getOriginalFilename())
                            .fileType(file.getContentType())
                            .fileSize(file.getSize())
                            .build());
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocumentResponse.builder()
                            .fileName(file != null ? file.getOriginalFilename() : "unknown")
                            .fileType(file != null ? file.getContentType() : "unknown")
                            .fileSize(file != null ? file.getSize() : 0)
                            .build());
        }
    }


    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.findAll());
    }

    @GetMapping("/{documentId}/preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable UUID documentId) {
        var document = documentService.download(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.fileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, document.getContentDisposition())
                .body(document.fileData());
    }
    
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID documentId) {
        DocumentResponse document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/user/{userId}")
//    @PreAuthorize("isAuthenticated()")
    public List<DocumentResponse> getDocumentsByUser(@PathVariable UUID userId) {
        return documentService.findByUploadedBy(userId);
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
