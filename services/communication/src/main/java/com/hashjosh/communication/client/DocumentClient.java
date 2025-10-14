package com.hashjosh.communication.client;

import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class DocumentClient {

    private final RestClient restClient;
    @Value("${spring.application.name}")
    private String applicationName;

    public DocumentClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:8020/api/v1/documents")
                .build();
    }

    public DocumentResponse getDocument(UUID documentId){
        try {
            return restClient.get()
                    .uri("/{documentId}",documentId)
                    .header("X-Internal-Service",applicationName)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new RuntimeException(
                                        "Failed to fetch document. Status: " + response.getStatusCode() +
                                                " Body: " + response.getBody()
                                );
                            }
                    )
                    .body(DocumentResponse.class);
        }catch (Exception e){
            log.error("Error fetching document with id: {}", documentId, e);
            throw new RuntimeException(
                    "Error fetching document: " + e.getMessage()
            );
        }
    }

    public String getDocumentPreviewUrl(UUID documentId){
        try {
            String url = restClient.get()
                    .uri("/{documentId}/view",documentId)
                    .header("X-Internal-Service", applicationName)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw new RuntimeException(
                                        "Failed to fetch document preview URL. Status: " + response.getStatusCode() +
                                                " Body: " + response.getBody()
                                );
                            }
                    )
                    .body(String.class);
            log.debug("Successfully retrieved preview URL for document: {}", documentId);
            return url;
        }catch (Exception e){
            log.error("Error fetching document preview URL with id: {}", documentId, e);
            throw new RuntimeException(
                    "Error fetching document preview URL: " + e.getMessage()
            );
        }
    }

}
