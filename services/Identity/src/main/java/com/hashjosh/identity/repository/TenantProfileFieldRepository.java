package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.Tenant;
import com.hashjosh.identity.entity.TenantProfileField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TenantProfileFieldRepository extends JpaRepository<TenantProfileField, UUID> {
    List<TenantProfileField> findByTenantId(UUID tenantId);

    boolean existsByTenantAndFieldKey(Tenant tenant, String fieldKey);
}
