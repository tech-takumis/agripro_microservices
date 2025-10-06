package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.FileUploadException;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class DocumentServiceClient {

    private final RestClient restClient;

    @Value("${spring.application.name}")
    private String applicationName;

    public DocumentServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8020/api/v1/documents")
                .build();
    }


    public DocumentResponse getDocument(String token, UUID documentId) {
        try {
            return restClient.get()
                    .uri("/info/{documentId}", documentId)
                    .header("X-Internal-Service", applicationName)
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
            log.error("Error fetching document with id: {}", documentId, ex);
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
