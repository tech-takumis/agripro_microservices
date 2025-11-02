package com.hashjosh.insurance.clients;

import com.hashjosh.insurance.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class FastAIClient {

    private final RestClient restClient;

    public FastAIClient(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder
                .baseUrl("http://fastai-service:8000")
                .build();
    }

    public AIResultDTO getAIResult(int resultId) {
        return restClient.get()
                .uri("/ai/{result_id}",resultId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, res) -> {
                            log.error("Failed to fetch farmers, status code: {}", res.getStatusCode());
                            throw ApiException.internalError("Failed to fetch AI result");
                        }
                )
                .body(AIResultDTO.class);
    }
}
