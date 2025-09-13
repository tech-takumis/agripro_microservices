package com.hashjosh.users.interceptor;

import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.users.properties.TenantProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantProperties tenantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = extractTenant(request);
        if (tenantId != null && tenantProperties.getTenants().containsKey(tenantId)) {
            TenantContext.setTenantId(tenantId);
            log.debug("Resolved tenant from request: {}", tenantId);
        } else {
            TenantContext.setTenantId(tenantProperties.getDefaultTenant());
            log.debug("Falling back to default tenant: {}", tenantProperties.getDefaultTenant());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContext.clear();
    }

    private String extractTenant(HttpServletRequest request) {
        String tenant = request.getHeader("X-Tenant-ID");
        if (tenant == null || tenant.isBlank()) {
            String domain = request.getServerName();
            if (domain.contains(".")) {
                tenant = domain.substring(0, domain.indexOf('.'));
            }
        }
        return tenant;
    }
}
