package com.hashjosh.application.clients;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomRestClientErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Detect if  response has a 4xx and 5xx code
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

}
