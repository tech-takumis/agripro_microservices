package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

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

    public String generatePresignedUrl(String userId,UUID documentId, int expiry) {
        return restClient.get()
                .uri("/{id}/download-url?expiryMinutes={expiry}", documentId, expiry)
                .header("X-Internal-Service", applicationName)
                .header("X-User-Id",userId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw ApiException.internalError("Failed to generate presigned url for document: " + documentId);
                        }
                )
                .body(String.class);
    }

    public DocumentResponse uploadDocument(MultipartFile file, String userId) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    })
                    .header("Content-Type", file.getContentType());

            return restClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .header("X-Internal-Service", applicationName)
                    .header("X-User-Id",userId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw ApiException.internalError("Failed to upload document: " + file.getOriginalFilename());
                            }
                    )
                    .body(DocumentResponse.class);
        } catch (RestClientException | java.io.IOException e) {
            log.error("Error uploading document: {}", file.getOriginalFilename(), e);
            throw ApiException.internalError("Failed to upload document: " + file.getOriginalFilename());
        }
    }
}
