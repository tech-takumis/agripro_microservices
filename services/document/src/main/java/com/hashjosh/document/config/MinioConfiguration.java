package com.hashjosh.document.config;

import com.hashjosh.document.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.url())
                .credentials(minioProperties.accessKey(), minioProperties.secretKey())
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initBucket() throws Exception {
        MinioClient client = minioClient();
        boolean found = client.bucketExists(
                BucketExistsArgs.builder().bucket(minioProperties.bucket()).build()
        );
        if (!found) {
            client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket()).build());
        }
    }

}
