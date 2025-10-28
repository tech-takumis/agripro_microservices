package com.hashjosh.verification.clients;


import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.verification.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class ApplicationClient {

    private final RestClient restClient;
    @Value("${spring.application.name}")
    private String applicationName;

    public ApplicationClient(RestClient.Builder clientBuilder) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8010/api/v1/applications")
                .build();
    }

    public ApplicationResponseDto getApplicationById(UUID applicationId,String userId) {
        return restClient.get()
                .uri("/{application-id}", applicationId)
                .header("X-Internal-Service", applicationName)
                .header("X-User-Id", userId)
                .exchange((req, res) -> {
                    if(res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(ApplicationResponseDto.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw ApiException.notFound("Application not found with ID: " + applicationId);
                    }else{
                        throw new RuntimeException("Remote Application Service error in workflow service find workflow by application id: "+ applicationId +" status"+ res.getStatusCode());
                    }
                });
    }
}
