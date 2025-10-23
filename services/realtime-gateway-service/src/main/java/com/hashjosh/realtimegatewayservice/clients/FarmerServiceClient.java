package com.hashjosh.realtimegatewayservice.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
public class FarmerServiceClient {

    private final RestClient restClient;

    public FarmerServiceClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:8020/api/v1/farmers")
                .build();
    }

    @Value("${spring.application.name}")
    private String applicationName;

    public FarmerResponse getFarmerById(UUID farmerId) {
        return restClient.get()
                .uri("/{farmer-id}", farmerId)
                .header("X-Internal-Service", applicationName)
                .retrieve()
                .body(FarmerResponse.class);
    }

    public List<FarmerResponse> getAllFarmers() {
        return restClient.get()
                .header("X-Internal-Service", applicationName)
                .retrieve()
                .body(new ParameterizedTypeReference<List<FarmerResponse>>() {});
    }
}
