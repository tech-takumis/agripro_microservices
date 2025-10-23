package com.hashjosh.document.config;

import com.hashjosh.document.exception.ApiException;
import com.hashjosh.document.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        try {
            // Configure HTTP client with timeouts
            OkHttpClient httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .writeTimeout(Duration.ofSeconds(30))
                    .readTimeout(Duration.ofSeconds(30))
                    .build();

            return MinioClient.builder()
                    .endpoint(minioProperties.urlInternal())
                    .credentials(minioProperties.accessKey(), minioProperties.secretKey())
                    .httpClient(httpClient)
                    .build();
        } catch (Exception e) {
            log.error("Error creating MinIO client: {}", e.getMessage(), e);
            throw ApiException.internalError("Failed to create MinIO client: " + e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initBucket() {
        try {
            MinioClient client = minioClient();
            boolean found = client.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioProperties.bucket())
                            .build()
            );

            if (!found) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.bucket())
                        .build());
                log.info("Created MinIO bucket: {}", minioProperties.bucket());
            } else {
                log.info("Using existing MinIO bucket: {}", minioProperties.bucket());
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage(), e);
            throw ApiException.internalError("Failed to initialize MinIO bucket: " + e.getMessage());
        }
    }
}
