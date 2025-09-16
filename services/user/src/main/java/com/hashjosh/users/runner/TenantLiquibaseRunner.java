package com.hashjosh.users.runner;

import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.users.properties.TenantProperties;
import jakarta.annotation.PostConstruct;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component // Use @Component instead of @Configuration to avoid bean conflicts
@RequiredArgsConstructor
@Slf4j
public class TenantLiquibaseRunner {

    private final TenantProperties tenantProperties;
    private final DataSource dataSource; // Inject TenantRoutingDataSource

    @PostConstruct
    public void migrateTenants() {
        log.info("Starting Liquibase migrations for tenants: {}", tenantProperties.getTenants().keySet());

        for (String tenantId : tenantProperties.getTenants().keySet()) {
            TenantContext.setTenantId(tenantId);
            log.info("Set tenant context to '{}' for Liquibase migration", tenantId);

            try (Connection conn = dataSource.getConnection()) {
                log.debug("Successfully connected to database for tenant: {}", tenantId);

                // Run Liquibase migration
                Liquibase liquibase = new Liquibase(
                        "db/changelog/changelog-master.yaml",
                        new ClassLoaderResourceAccessor(),
                        new JdbcConnection(conn)
                );
                liquibase.update(new Contexts(), new LabelExpression());
                log.info("✅ Liquibase migration applied for tenant: {}", tenantId);
            } catch (SQLException e) {
                log.error("Failed to connect to database for tenant {}: {}", tenantId, e.getMessage(), e);
                continue; // Skip to next tenant
            } catch (Exception e) {
                log.error("Failed to apply Liquibase migration for tenant {}: {}", tenantId, e.getMessage(), e);
            } finally {
                TenantContext.clear();
                log.info("Cleared tenant context for '{}'", tenantId);
            }
        }
        log.info("Completed Liquibase migrations for all tenants.");
    }
}