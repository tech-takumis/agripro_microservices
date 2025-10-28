package com.hashjosh.pcic.clients;

import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.pcic.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class ApplicationServiceClient {
    private final RestClient restClient;

    public ApplicationServiceClient(RestClient.Builder clientBuilder) {
        this.restClient = RestClient.builder()
                .baseUrl("http://application-service/api/v1/applications")
                .build();
    }

    public ApplicationResponseDto getApplicationById(String token, UUID applicationId, HttpServletRequest request) {
        return restClient.get()
                .uri("/{application-id}", applicationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .exchange((req, res) -> {
                    if(res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(ApplicationResponseDto.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw ApiException.notFound("Application not found");
                    }else{
                        throw ApiException.internalError("Failed to fetch application details");
                    }
                });
    }
}
