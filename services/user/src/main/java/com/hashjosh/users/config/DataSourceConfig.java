package com.hashjosh.users.config;

import com.hashjosh.users.properties.TenantProperties;
import com.hashjosh.users.services.TenantRoutingDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final TenantProperties tenantProperties;

    @Bean
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        tenantProperties.getTenants().forEach((tenantId, dsProps) -> {
            DataSourceProperties props = new DataSourceProperties();
            props.setUrl(dsProps.getUrl());
            props.setUsername(dsProps.getUsername());
            props.setPassword(dsProps.getPassword());
            props.setDriverClassName(dsProps.getDriverClassName());
            targetDataSources.put(tenantId, props.initializeDataSourceBuilder().build());
        });

        TenantRoutingDataSource dataSource = new TenantRoutingDataSource();
        dataSource.setDefaultTargetDataSource(
                targetDataSources.get(tenantProperties.getDefaultTenant())
        );
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.afterPropertiesSet();
        return dataSource;
    }
}

