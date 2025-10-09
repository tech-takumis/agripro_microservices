package com.hashjosh.document.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        String urlInternal,
        String urlExternal,
        String accessKey,
        String secretKey,
        String bucket
) {
}
