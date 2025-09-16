package com.hashjosh.users.config;

import com.hashjosh.users.properties.TenantProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class MultiTenantConfig {

    @Bean
    @Primary
    public DataSource multiTenantDataSource(TenantProperties tenantProperties) {
        Map<Object, Object> dataSources = new HashMap<>();

        for (Map.Entry<String, TenantProperties.DataSourceProperties> entry :
                tenantProperties.getTenants().entrySet()) {
            String tenantId = entry.getKey();
            TenantProperties.DataSourceProperties props = entry.getValue();
            log.info("Creating DataSource for tenant '{}': {}", tenantId, props.getUrl());
            dataSources.put(tenantId, createDataSource(props));
        }

        String defaultTenant = tenantProperties.getDefaultTenant();
        TenantProperties.DataSourceProperties defaultTenantProps = tenantProperties.getTenants().get(defaultTenant);
        if (defaultTenantProps == null) {
            throw new IllegalStateException("Default tenant '" + defaultTenant + "' not found in tenant configurations");
        }
        log.info("Default tenant '{}': {}", defaultTenant, defaultTenantProps.getUrl());
        DataSource defaultDataSource = createDataSource(defaultTenantProps);

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource(defaultDataSource, dataSources);
        log.info("Initialized TenantRoutingDataSource with tenants: {}", dataSources.keySet());
        return routingDataSource;
    }

    private DataSource createDataSource(TenantProperties.DataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        return dataSource;
    }
}