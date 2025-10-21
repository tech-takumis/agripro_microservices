package com.hashjosh.realtimegatewayservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Slf4j
public class UserClient {
    private final RestClient restClient;
    @Value("${spring.application.name}")
    private  String applicationName;

    public UserClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:9010/api/v1/agriculture")
                .build();
    }

    public String getAgricultureName(UUID id){
        return restClient.get()
                .uri("/{id}/name",id)
                .header("X-Internal-Service",applicationName)
                .retrieve()
                .body(String.class);
    }

    public boolean userExists(UUID senderId) {
        return !Boolean.TRUE.equals(restClient.get()
                .uri("/{id}/exists", senderId)
                .header("X-Internal-Service", applicationName)
                .retrieve()
                .body(Boolean.class));
    }
}
