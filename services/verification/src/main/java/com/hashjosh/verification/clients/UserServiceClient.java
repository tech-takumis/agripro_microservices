package com.hashjosh.verification.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.verification.exception.VerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://user-service/api/v1/users")
                .build();
    }

    public UserResponse getUserById(UUID userId, String token) {
        return restClient.get()
                .uri("/{user-id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange((req,res) -> {
                    if (res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(UserResponse.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new VerificationException(
                            "User id "+userId+" not found!",
                                HttpStatus.NOT_FOUND.value()
                        );
                    }else{
                       throw  new VerificationException(
                               "Failed to get user id " + userId + "status code: "+ res.getStatusCode(),
                               HttpStatus.BAD_REQUEST.value()
                       );
                    }
                });

    }



}
