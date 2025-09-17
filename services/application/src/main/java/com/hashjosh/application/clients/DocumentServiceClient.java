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

    public DocumentResponse uploadDocument(String token,
                                           UUID applicationId,
                                           UUID uploadedBy,
                                           MultipartFile file) throws IOException {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("applicationId", applicationId.toString());
        builder.part("uploadedBy", uploadedBy.toString());
        builder.part("file", new MultipartFileResource(file))
                .filename(Objects.requireNonNull(file.getOriginalFilename()))
                .contentType(MediaType.parseMediaType(
                        Objects.requireNonNullElse(file.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE)
                ));
        // 1️⃣ Retrieve raw string instead of directly mapping
        ResponseEntity<String> rawResponse = restClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .toEntity(String.class);

        // 2️⃣ Log raw response for debugging
        log.info("Document service response: status={} body={}",
                rawResponse.getStatusCode(), rawResponse.getBody());

        // 3️⃣ Handle non-2xx explicitly
        if (!rawResponse.getStatusCode().is2xxSuccessful()) {
            throw new FileUploadException(
                    "Failed to upload document. Status: " + rawResponse.getStatusCode() +
                            " Body: " + rawResponse.getBody(),
                    rawResponse.getStatusCode().value(),
                    "/api/v1/documents"
            );
        }

        // 4️⃣ Attempt JSON deserialization
        try {
            return objectMapper.readValue(rawResponse.getBody(), DocumentResponse.class);
        } catch (Exception ex) {
            throw new FileUploadException("Invalid response format from document service: " + rawResponse.getBody(),500,"asda");
        }
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
}
