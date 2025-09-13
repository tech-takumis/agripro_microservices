package com.hashjosh.verification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class VerificationTopicConfiguration {

    @Bean
    public NewTopic verificationTopic() {
        return TopicBuilder
                .name("verification-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic verificationTopicDlt(){
        return TopicBuilder
                .name("verification-events-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
