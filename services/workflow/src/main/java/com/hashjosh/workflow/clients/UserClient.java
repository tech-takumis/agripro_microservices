package com.hashjosh.workflow.clients;

import com.hashjosh.workflow.dto.UserResponseDto;
import com.hashjosh.workflow.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://user-service/api/v1/users")
                .build();
    }


    public UserResponseDto findUserById(String token, UUID updatedBy, HttpServletRequest request) {
        return restClient.get()
                .uri("/{id}", updatedBy)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .exchange((req, res) -> {

                    if(res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(UserResponseDto.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new UserNotFoundException(
                                "User with id :::"+updatedBy+" not found exception!",
                                HttpStatus.NOT_FOUND.value(),
                                request.getRequestURI()

                        );
                    }else {
                        System.out.println("Response body: " + res.bodyTo(String.class)); // Add this
                        throw new RuntimeException("Remote Application Service error int workflow service update workflow status function status code::: " + res.getStatusCode());
                    }
                });
    }
}
