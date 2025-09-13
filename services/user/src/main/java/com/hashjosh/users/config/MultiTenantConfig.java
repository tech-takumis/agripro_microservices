package com.hashjosh.users.config;

import com.hashjosh.users.properties.TenantProperties;
import com.hashjosh.users.services.TenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultiTenantConfig {

    @Bean
    @Primary
    public DataSource multiTenantDataSource(TenantProperties tenantProperties) {
        Map<Object, Object> dataSources = new HashMap<>();

        for (Map.Entry<String, TenantProperties.DataSourceProperties> entry :
                tenantProperties.getTenants().entrySet()) {
            dataSources.put(entry.getKey(), createDataSource(entry.getValue()));
        }

        TenantProperties.DataSourceProperties defaultTenantProps =
                tenantProperties.getTenants().get(tenantProperties.getDefaultTenant());
        DataSource defaultDataSource = createDataSource(defaultTenantProps);

        return new TenantRoutingDataSource(defaultDataSource, dataSources);
    }

    private DataSource createDataSource(TenantProperties.DataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        return dataSource;
    }
}

