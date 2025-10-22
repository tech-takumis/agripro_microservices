package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.Role;
import com.hashjosh.identity.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameAndTenant(String name, Tenant tenant);

    Optional<Role> findByTenantIdAndName(UUID tenantId, String name);
    Optional<Role> findByTenantKeyIgnoreCaseAndNameIgnoreCase(String tenantKey, String name);
    Optional<Role> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Role> findByTenantAndNameContainingIgnoreCase(Tenant tenant, String namePart);
}
