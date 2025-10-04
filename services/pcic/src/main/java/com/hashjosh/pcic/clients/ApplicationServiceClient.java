package com.hashjosh.pcic.clients;

import com.hashjosh.pcic.exception.ApplicationNotFoundException;
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
                        throw new ApplicationNotFoundException(
                                "Application not found in Application Service!",
                                HttpStatus.NOT_FOUND.value()
                        );
                    }else{
                        throw new RuntimeException("Remote Application Service error in workflow service find workflow by application id: "+ applicationId +" status"+ res.getStatusCode());
                    }
                });
    }
}
