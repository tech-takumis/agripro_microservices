package com.hashjosh.application.clients;

import com.hashjosh.application.exceptions.DocumentNotFoundException;
import com.hashjosh.application.exceptions.ExternalServiceException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CustomRestClientErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Detect if  response has a 4xx and 5xx code
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

        // Map 4xx and 5xx for meaningful exception
        if (status.is4xxClientError()) {
            if (status.value() == 404) {
                throw new DocumentNotFoundException("Document not found: " + body);
            } else {
                throw new ExternalServiceException("Client error from document-service: " + body);
            }
        } else if (status.is5xxServerError()) {
            throw new ExternalServiceException("Server error from document-service: " + body);
        }
    }
}
