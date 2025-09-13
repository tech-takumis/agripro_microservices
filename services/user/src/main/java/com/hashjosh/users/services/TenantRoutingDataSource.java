package com.hashjosh.users.services;

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
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            log.debug("No tenant found in context, using default data source");
        } else {
            log.debug("Using tenant: {}", tenantId);
        }
        return tenantId;
    }
}

