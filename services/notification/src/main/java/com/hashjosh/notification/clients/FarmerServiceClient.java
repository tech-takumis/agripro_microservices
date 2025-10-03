package com.hashjosh.notification.clients;

import jakarta.ws.rs.client.ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmerServiceClient {

    private ClientBuilder builder;

    public FarmerServiceClient() {}
}
