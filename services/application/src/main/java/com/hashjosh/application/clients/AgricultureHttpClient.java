package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.constant.verification.VerificationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class AgricultureHttpClient {

    private  final RestClient restClient;
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USER_ID_HEADER = "X-User-Id";
    @Value("${spring.application.name}")
    private String applicationName;

    public AgricultureHttpClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8030/api/v1")
                .build();
    }

    public void submitApplication(VerificationRequestDto dto, String userId) {
        try{
            String response =  restClient.post()
                    .uri("/agriculture/verification")
                    .header(INTERNAL_SERVICE_HEADER, applicationName)
                    .header(USER_ID_HEADER,userId)
                    .body(dto)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, res) -> {
                                throw new RuntimeException(
                                        "Failed to upload document. Status: " + res.getStatusCode() +
                                                " Body: " + res.getBody()
                                );
                            }
                    )
                    .body(String.class);
            log.info("Successfully submitted application to Agriculture service: {}", response);
        }catch (RestClientException e){
            log.error("Error submitting application to Agriculture service: {}", e.getMessage());
            throw ApiException.internalError("Failed to submit application to Agriculture service");
        }
    }
}
