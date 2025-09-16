package com.hashjosh.users.config;

import com.hashjosh.jwtshareable.service.TenantContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

@NoArgsConstructor
@Slf4j
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    public TenantRoutingDataSource(DataSource defaultDataSource, Map<Object, Object> targetDataSources) {
        log.info("Initializing TenantRoutingDataSource with default DataSource and tenants: {}", targetDataSources.keySet());
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        log.debug("Determining lookup key, current tenant: {}", tenantId != null ? tenantId : "default (null)");
        return tenantId != null ? tenantId : "default";
    }
}