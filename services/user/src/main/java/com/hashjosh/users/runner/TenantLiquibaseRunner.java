package com.hashjosh.users.runner;

import com.hashjosh.users.config.MultiTenantConfig;
import com.hashjosh.users.properties.TenantProperties;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TenantLiquibaseRunner {

    private final TenantProperties tenantProperties;
    private final MultiTenantConfig multiTenantConfig;

    @PostConstruct
    public void migrateTenants() throws Exception {
        for (Map.Entry<String, TenantProperties.DataSourceProperties> entry :
                tenantProperties.getTenants().entrySet()) {
            String tenantId = entry.getKey();
            TenantProperties.DataSourceProperties dsConfig = entry.getValue();

            // Use MultiTenantConfig's helper logic to build a DataSource for this tenant
            DataSource dataSource = buildTenantDataSource(dsConfig);

            try (Connection conn = dataSource.getConnection()) {
                Liquibase liquibase = new Liquibase(
                        "db/changelog/changelog-master.yaml",
                        new ClassLoaderResourceAccessor(),
                        new JdbcConnection(conn)
                );
                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println("✅ Liquibase migration applied for tenant: " + tenantId);
            }
        }
    }

    /**
     * Reuse the same HikariCP settings as MultiTenantConfig to build a DataSource.
     */
    private DataSource buildTenantDataSource(TenantProperties.DataSourceProperties properties) {
        // Option 1: Call the same logic as MultiTenantConfig by copying its method
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setUsername(properties.getUsername());
        ds.setPassword(properties.getPassword());
        ds.setDriverClassName(properties.getDriverClassName());
        return ds;
    }
}