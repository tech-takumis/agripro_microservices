package com.hashjosh.users.interceptors;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.users.properties.TenantProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {
    private final TenantProperties tenantProperties;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tenantId = extractTenant(request);
        if (tenantId != null && tenantProperties.getTenants().containsKey(tenantId)) {
            TenantContext.setTenantId(tenantId);
            log.info("Resolved tenant from request: {}", tenantId);
        } else {
            String defaultTenant = tenantProperties.getDefaultTenant();
            TenantContext.setTenantId(defaultTenant);
            log.info("Falling back to default tenant: {}", defaultTenant);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContext.clear();
        log.debug("Cleared tenant context after request");
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

        // Fallback to domain
        String domain = request.getServerName();
        if (domain.contains(".")) {
            tenant = domain.substring(0, domain.indexOf('.'));
            log.debug("Extracted tenantId from domain: {}", tenant);
            return tenant;
        }

        log.debug("No tenantId extracted from request");
        return null;
    }

    private String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Extracted JWT from Authorization header");
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            String jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> "ACCESS_TOKEN".equals(cookie.getName()))
                    .map(jakarta.servlet.http.Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            if (jwt != null) {
                log.debug("Extracted JWT from ACCESS_TOKEN cookie");
                return jwt;
            }
        }

        log.debug("No JWT found in request");
        return null;
    }
}