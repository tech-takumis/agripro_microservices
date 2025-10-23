package com.hashjosh.realtimegatewayservice.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmerServiceClient {

    private final RestClient farmerRestClient;

    @Value("${spring.application.name}")
    private String applicationName;

    public FarmerResponse getFarmerById(UUID farmerId) {
        return farmerRestClient.get()
                .uri("/{farmer-id}", farmerId)
                .header("X-Internal-Service", applicationName)
                .retrieve()
                .body(FarmerResponse.class);
    }

    public List<FarmerResponse> getAllFarmers() {
        return farmerRestClient.get()
                .header("X-Internal-Service", applicationName)
                .retrieve()
                .body(new ParameterizedTypeReference<List<FarmerResponse>>() {});
    }
}
