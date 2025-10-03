package com.hashjosh.farmer.clients;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RsbsaServiceClient {

    private final RestClient restClient;

    public RsbsaServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://rsbsa-service/api/v1/public/rsbsa")
                .build();
    }

    public RsbsaResponseDto getRsbsaById(String rsbsaId) {
        return restClient.get()
                .uri("/{rsbsa-id}", rsbsaId)
                .retrieve()
                .body(RsbsaResponseDto.class);
    }
}
