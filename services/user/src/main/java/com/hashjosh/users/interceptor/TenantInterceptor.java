package com.hashjosh.users.interceptors;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.users.properties.TenantProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);
    private final TenantProperties tenantProperties;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
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
        // Try to extract tenantId from JWT
        String jwt = extractJwt(request);
        if (jwt != null) {
            try {
                Claims claims = jwtService.getAllClaims(jwt);
                String tenantId = claims.get("tenantId", String.class);
                if (tenantId != null) {
                    log.debug("Extracted tenantId from JWT: {}", tenantId);
                    return tenantId;
                }
            } catch (Exception e) {
                log.warn("Failed to parse JWT for tenantId: {}", e.getMessage());
            }
        }

        // Fallback to X-Tenant-ID header
        String tenant = request.getHeader("X-Tenant-ID");
        if (tenant != null && !tenant.isBlank()) {
            log.debug("Extracted tenantId from X-Tenant-ID header: {}", tenant);
            return tenant;
        }

        // Fallback to domain (optional, remove if not needed)
        String domain = request.getServerName();
        if (domain.contains(".")) {
            tenant = domain.substring(0, domain.indexOf('.'));
            log.debug("Extracted tenantId from domain: {}", tenant);
            return tenant;
        }

        return null;
    }

    private String extractJwt(HttpServletRequest request) {
        // Check Authorization header (Bearer token)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Check ACCESS_TOKEN cookie (for pcic and agriculture)
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "ACCESS_TOKEN".equals(cookie.getName()))
                    .map(jakarta.servlet.http.Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}