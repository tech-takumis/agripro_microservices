package com.hashjosh.workflow.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class WorkflowTopicConfiguration {

    @Bean
    public NewTopic documentTopic() {
        return TopicBuilder
                .name("workflow-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic documentTopicDlt() {
        return TopicBuilder
                .name("workflow-events-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
