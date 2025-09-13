package com.hashjosh.document.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class DocumentTopicConfiguration {

    @Bean
    public NewTopic documentTopic() {
        return TopicBuilder
                .name("document-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic documentTopicDlt() {
        return TopicBuilder
                .name("document-events-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
