package com.hashjosh.communication.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class AgricultureClient {

    private final RestClient restClient;
    @Value("${spring.application.name}")
    private  String applicationName;

    public AgricultureClient(RestClient.Builder builder){
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
}
