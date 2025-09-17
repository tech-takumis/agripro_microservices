package com.hashjosh.application.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.dto.DocumentResponse;
import com.hashjosh.application.exceptions.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class DocumentServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DocumentServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://document-service/api/v1/documents")
                .build();
    }


    public DocumentResponse getDocument(String token, UUID documentId) {
        try {
            return restClient.get()
                    .uri("/info/{documentId}", documentId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new FileUploadException(
                                        "Failed to fetch document. Status: " + response.getStatusCode() +
                                                " Body: " + response.getBody(),
                                        response.getStatusCode().value(),
                                        "/api/v1/documents/" + documentId + "/info"
                                );
                            }
                    )
                    .body(DocumentResponse.class);
        } catch (Exception ex) {
            log.error("Error fetching document with id: " + documentId, ex);
            throw new FileUploadException(
                    "Error fetching document: " + ex.getMessage(),
                    500,
                    "/api/v1/documents/" + documentId + "/info"
            );
        }
    }

    public boolean documentExists(String token, UUID documentId) {
        try {
            restClient.head()
                    .uri("/{documentId}", documentId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            log.debug("Document not found: {}", documentId);
            return false;
        }
    }
}
