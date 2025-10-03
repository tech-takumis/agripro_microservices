package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.ClientBatchException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AgricultureServiceClient {

    private final RestClient restClient;

    public AgricultureServiceClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://agriculture-service/api/v1")
                .build();
    }

    public BatchResponse getOpenBatch() {
        try {
            BatchResponse batchResponse = restClient.get()
                    .uri("/batches/open")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 400) {
                            response.getBody();
                            String errorMessage = new String(response.getBody().readAllBytes());
                            throw new ClientBatchException(errorMessage);
                        } else if (response.getStatusCode().value() == 404) {
                            throw new ClientBatchException("No open and non-expired batch found");
                        } else {
                            throw new ClientBatchException("Client error: " + response.getStatusText());
                        }
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new ClientBatchException("Server error while fetching open batch: " + response.getStatusText());
                    })
                    .body(BatchResponse.class);

            if (batchResponse == null) {
                throw new ClientBatchException("Received null response from Batch Service");
            }
            if (!isValidStatus(String.valueOf(batchResponse.getStatus()))) {
                throw new ClientBatchException("Invalid batch status: " + batchResponse.getStatus());
            }
            return batchResponse;
        } catch (ClientBatchException e) {
            throw e; // Re-throw specific batch exceptions
        } catch (Exception e) {
            throw new ClientBatchException("Failed to fetch open batch", e);
        }
    }

    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals("OPEN") ||
                        status.equals("CLOSED") ||
                        status.equals("VERIFIED")
        );
    }

}
