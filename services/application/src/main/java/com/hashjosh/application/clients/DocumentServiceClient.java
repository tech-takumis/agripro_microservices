package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    public DocumentResponse uploadDocument(MultipartFile file, String userId){
        try{
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            }).header(HttpHeaders.CONTENT_TYPE, file.getContentType());

            DocumentResponse response =  restClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .header("X-Internal-Service", applicationName)
                    .header("X-User-Id",userId)
                    .retrieve()
                    .onStatus(
                            httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                            (request,res) -> {
                                throw ApiException.internalError(
                                        "Failed to upload document. Status: " + res.getStatusCode() +
                                                " Body: " + res.getBody()
                                );
                            })
                    .body(DocumentResponse.class);
            log.info("Successfully uploaded document");
            return response;
        }catch (RestClientException | IOException e){
            log.error("Error uploading document", e);
            throw ApiException.internalError("Failed to upload document");
        }
    }




    public boolean documentExists(UUID documentId) {
        try {
            restClient.head()
                    .uri("/{documentId}", documentId)
                    .header("X-Internal-Service",applicationName)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            log.debug("Document not found: {}", documentId);
            return false;
        }
    }
    public String generatePresignedUrl(UUID documentId, int expiry) {
        try{
            String url = restClient.get()
                    .uri("/{id}/download-url?expiryMinutes={expiry}", documentId, expiry)
                    .header("X-Internal-Service", applicationName)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                throw ApiException.internalError(
                                        "Failed to fetch document preview URL. Status: " + response.getStatusCode() +
                                                " Body: " + response.getBody()
                                );
                            }
                    )
                    .body(String.class);
            log.debug("Successfully retrieved preview URL for document: {}", documentId);
            return url;
        }catch (Exception e){
            log.error("Error generating presigned URL for document id: {}", documentId, e);
            throw ApiException.notFound("Document not found with id: " + documentId);
        }
    }



}
