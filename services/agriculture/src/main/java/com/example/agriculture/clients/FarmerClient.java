package com.example.agriculture.clients;

import com.example.agriculture.exception.ApiException;
import com.hashjosh.constant.farmer.FarmerReponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class FarmerClient {

    private final RestClient restClient;
    private static final String BASE_URL = "http://localhost:9020/api/v1/farmer";
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";
    @Value( "${spring.application.name}")
    private String applicationName;

    public FarmerClient(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder
                .baseUrl(BASE_URL)
                .build();

    }

    public List<FarmerReponse> getAllFarmers(String userId) {
        try {
            List<FarmerReponse> responses = restClient.get()
                    .uri("")
                    .header(INTERNAL_SERVICE_HEADER, applicationName)
                    .header(USERID_HEADER, userId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, res) -> {
                                log.error("Failed to fetch farmers, status code: {}", res.getStatusCode());
                                throw ApiException.internalError("Failed to fetch farmers");
                            }
                    )
                    .body(new ParameterizedTypeReference<List<FarmerReponse>>() {});

            log.info("Successfully fetched farmers: {}", responses);
            return responses;
        } catch (Exception e) {
            log.error("Error fetching farmers: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch farmers", e);
        }
    }

    public FarmerReponse getFarmerById(String farmerId, String userId) {
        try {
            FarmerReponse response = restClient.get()
                    .uri("/{farmer-id}" + farmerId)
                    .header(INTERNAL_SERVICE_HEADER, applicationName)
                    .header(USERID_HEADER, userId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, res) -> {
                                log.error("Failed to fetch farmer with ID {}, status code: {}", farmerId, res.getStatusCode());
                                throw ApiException.internalError("Failed to fetch farmer");
                            }
                    )
                    .body(FarmerReponse.class);

            log.info("Successfully fetched farmer: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching farmer with ID {}: {}", farmerId, e.getMessage());
            throw new RuntimeException("Failed to fetch farmer", e);
        }
    }


}
