package com.hashjosh.gateway.service;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class TenantContext {
    private static final String TENANT_KEY = "TENANT_ID";

    public static Context setTenant(Context context, String tenantId) {
        return context.put(TENANT_KEY, tenantId);
    }

    public static Mono<String> getTenant() {
        return Mono.deferContextual(ctx ->
                ctx.hasKey(TENANT_KEY) ? Mono.just(ctx.get(TENANT_KEY)) : Mono.empty()
        );
    }
}
