package com.hashjosh.farmer.config;


import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class RestClientConfiguration {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                    .setResponseTimeout(Timeout.ofSeconds(10))
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig).build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);

            builder.requestFactory(requestFactory);
        };
    }
}
