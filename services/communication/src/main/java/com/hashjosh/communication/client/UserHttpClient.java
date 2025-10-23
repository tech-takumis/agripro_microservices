package com.hashjosh.communication.client;

import com.hashjosh.constant.user.UserResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class UserHttpClient {

    private final RestClient restClient;
    @Value("${spring.application.name}")
    private  String applicationName;

    public UserHttpClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:9050/api/v1/users")
                .build();
    }

    public String getUserName(UUID id){
        return restClient.get()
                .uri("/{id}/name",id)
                .header("X-Internal-Service",applicationName)
                .header("X-User-Id",id.toString())
                .retrieve()
                .body(String.class);
    }

    public UserResponseDTO getUserById(UUID id){
        return restClient.get()
                .uri("/{id}",id)
                .header("X-Internal-Service",applicationName)
                .header("X-User-Id",id.toString())
                .retrieve()
                .body(UserResponseDTO.class);
    }
}
