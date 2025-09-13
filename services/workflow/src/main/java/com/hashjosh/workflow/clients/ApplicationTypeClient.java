package com.hashjosh.workflow.clients;


import com.hashjosh.workflow.dto.ApplicationTypeResponseDto;
import com.hashjosh.workflow.exceptions.ApplicationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class ApplicationTypeClient {

    private  final RestClient restClient;

    public ApplicationTypeClient(RestClient.Builder builder) {
         this.restClient = builder
                 .baseUrl("http://application-service/api/v1/application/types")
                 .build();
    }

    public ApplicationTypeResponseDto findApplicationTypeById(String token,UUID id, HttpServletRequest request) {
        return  restClient.get()
                .uri("/{id}",id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .exchange((req, res) -> {
                    if(res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(ApplicationTypeResponseDto.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new ApplicationNotFoundException(
                                "Application not found in Application Service!",
                                HttpStatus.NOT_FOUND.value(),
                                request.getRequestURI()
                        );
                    }else{
                        throw new RuntimeException("Remote Application Service error in workflow service find workflow by application id: " + res.getStatusCode());
                    }
                });
    }
}
