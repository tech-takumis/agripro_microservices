package com.hashjosh.insurance.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class InsuranceTopicConfiguration {

    @Bean
    public NewTopic insuranceTopic() {
        return TopicBuilder
                .name("insurance-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic insuranceTopicDlt() {
        return TopicBuilder
                .name("insurance-events-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
