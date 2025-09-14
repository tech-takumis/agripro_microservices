
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TenantLiquibaseRunner {

    private static final Logger logger = LoggerFactory.getLogger(TenantLiquibaseRunner.class);

    private final TenantProperties tenantProperties;
    private final MultiTenantConfig multiTenantConfig;

    @PostConstruct
    public void migrateTenants() {
        logger.info("Starting Liquibase migrations for tenants: {}", tenantProperties.getTenants().keySet());

        for (Map.Entry<String, TenantProperties.DataSourceProperties> entry : tenantProperties.getTenants().entrySet()) {
            String tenantId = entry.getKey();
            TenantProperties.DataSourceProperties dsConfig = entry.getValue();

            try {
                // Build DataSource for tenant
                DataSource dataSource = buildTenantDataSource(dsConfig);

                // Validate connection
                try (Connection conn = dataSource.getConnection()) {
                    logger.debug("Successfully connected to database for tenant: {}", tenantId);

                    // Run Liquibase migration
                    Liquibase liquibase = new Liquibase(
                            "db/changelog/changelog-master.yaml",
                            new ClassLoaderResourceAccessor(),
                            new JdbcConnection(conn)
                    );
                    liquibase.update(new Contexts(), new LabelExpression());
                    logger.info("✅ Liquibase migration applied for tenant: {}", tenantId);
                } catch (SQLException e) {
                    logger.error("Failed to connect to database for tenant {}: {}", tenantId, e.getMessage(), e);
                    continue; // Skip to next tenant
                }
            } catch (Exception e) {
                logger.error("Failed to apply Liquibase migration for tenant {}: {}", tenantId, e.getMessage(), e);
            }
        }
        logger.info("Completed Liquibase migrations for all tenants.");
    }

    /**
     * Reuse the same HikariCP settings as MultiTenantConfig to build a DataSource.
     */
    private DataSource buildTenantDataSource(TenantProperties.DataSourceProperties properties) {
        try {
            // Reuse MultiTenantConfig's DataSource creation if available
            DataSource dataSource = multiTenantConfig.createDataSource(properties);
            if (dataSource != null) {
                logger.debug("Using MultiTenantConfig to create DataSource for URL: {}", properties.getUrl());
                return dataSource;
            }
        } catch (Exception e) {
            logger.warn("Failed to use MultiTenantConfig for DataSource creation, falling back to default: {}", e.getMessage());
        }

        // Fallback to manual HikariDataSource creation
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setUsername(properties.getUsername());
        ds.setPassword(properties.getPassword());
        ds.setDriverClassName(properties.getDriverClassName());
        // Apply HikariCP settings from application.yml
        ds.setMaximumPoolSize(10);
        ds.setMinimumIdle(5);
        logger.debug("Created HikariDataSource for URL: {}", properties.getUrl());
        return ds;
    }
}
