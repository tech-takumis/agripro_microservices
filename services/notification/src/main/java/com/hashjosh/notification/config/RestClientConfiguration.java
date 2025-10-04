package com.hashjosh.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class RestClientConfiguration {

    /**
     * Shared RestClient.Builder bean with LoadBalancer support.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Customize HTTP timeouts and request factory.
     */
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                    .setResponseTimeout(Timeout.ofSeconds(10))
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);

            builder.requestFactory(requestFactory);
        };
    }

    /**
     * Dedicated RestClient for calling Farmer Service.
     * If you use service discovery (Eureka), replace localhost with the service-id.
     */
    @Bean
    public RestClient farmerRestClient(RestClient.Builder builder) {
        return builder
                 .baseUrl("http://localhost:9020/api/v1/farmer") // 🔹 if calling directly
                .build();
    }
}
