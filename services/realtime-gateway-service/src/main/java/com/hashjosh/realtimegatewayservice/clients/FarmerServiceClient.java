package com.hashjosh.realtimegatewayservice.clients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class FarmerServiceClient {

    private final RestClient restClient;

    public FarmerServiceClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:9020/api/v1/farmer")
                .build();
    }

    @Value("${spring.application.name}")
    private String applicationName;

    public FarmerResponse getFarmerById(UUID farmerId) {
        return restClient.get()
                .uri("/{farmer-id}", farmerId)
                .header("X-Internal-Service", applicationName)
                .header("X-User-Id", String.valueOf(farmerId))
                .retrieve()
                .body(FarmerResponse.class);
    }
}
