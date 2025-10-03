package com.hashjosh.notification.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmerServiceClient {

    private RestClient restClient;

    public FarmerServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://farmer-service/api/v1/public/farmer/")
                .build();
    }

    public FarmerResponse getFarmerById(UUID farmerId) {
        return restClient.get()
                .uri("/{farmer-id}", farmerId)
                .retrieve()
                .body(FarmerResponse.class);
    }

    public List<FarmerResponse> getAllFarmers() {
        return restClient.get()
                .retrieve()
                .body(new ParameterizedTypeReference<List<FarmerResponse>>() {});
    }
}
