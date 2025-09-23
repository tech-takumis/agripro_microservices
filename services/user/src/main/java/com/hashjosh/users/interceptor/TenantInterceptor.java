package com.hashjosh.users.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.jwtshareable.service.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId;

        // Identify if the request is for a public endpoint
        if (isPublicEndpoint(request)) {
            log.info("Public endpoint request detected: {}", request.getRequestURI());
        
            // Extract tenant ID from the X-Tenant-ID header
            tenantId = request.getHeader("X-Tenant-ID");
            if (tenantId == null || tenantId.isBlank()) {
                log.warn("No X-Tenant-ID header found for public request.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Return 400 if X-Tenant-ID is missing
                response.getWriter().write("X-Tenant-ID header is required for public endpoints.");
                return false;
            }
            log.info("Extracted tenant ID from X-Tenant-ID header for public endpoint: {}", tenantId);
        } else {
            // For non-public endpoints, extract tenant ID from JWT
            String jwt = extractJwt(request);
            if (jwt == null) {
                log.warn("No JWT found in request for protected endpoint.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 if JWT is missing
                response.getWriter().write("Authorization token is required for this endpoint.");
                return false;
            }

            // Extract tenant ID from JWT
            tenantId = extractTenantIdFromJwt(jwt);
            log.info("Extracted tenant ID from JWT: {}", tenantId);
        }

        // Store tenant ID in a ThreadLocal or context for downstream usage
        TenantContext.setTenantId(tenantId);

        return true;
    }

private boolean isPublicEndpoint(HttpServletRequest request) {
    String requestPath = request.getRequestURI();

    // Normalize paths to avoid mismatch issues
    return requestPath.equals("/api/v1/auth/login") ||
           requestPath.equals("/api/v1/auth/register") ||
           requestPath.startsWith("/public") || // Include any paths under /public
           requestPath.equals("/health");
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

private String extractTenantIdFromJwt(String jwt) {
    try {
        // Decode JWT and extract tenant ID
        String decodedPayload = new String(Base64.getDecoder().decode(jwt.split("\\.")[1]));
        com.fasterxml.jackson.databind.JsonNode payloadNode = new ObjectMapper().readTree(decodedPayload);

        // Look for both camel case and snake case versions of "tenantId"
        com.fasterxml.jackson.databind.JsonNode tenantIdNode = payloadNode.get("tenantId"); // camel case
        if (tenantIdNode == null) {
            tenantIdNode = payloadNode.get("tenant_id"); // snake case, fallback (optional)
        }

        if (tenantIdNode == null) {
            log.error("JWT does not contain tenantId or tenant_id field");
            throw new IllegalStateException("Invalid JWT: tenant ID not found");
        }

        return tenantIdNode.asText();
    } catch (Exception e) {
        log.error("Failed to extract tenant ID from JWT", e);
        throw new IllegalStateException("Invalid JWT", e);
    }
}
}