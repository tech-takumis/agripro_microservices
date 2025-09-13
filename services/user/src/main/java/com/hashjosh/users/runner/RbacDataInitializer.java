package com.hashjosh.users.runner;

import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.users.entity.Permission;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.properties.TenantProperties;
import com.hashjosh.users.repository.PermissionRepository;
import com.hashjosh.users.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RbacDataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final TenantProperties tenantProperties;
    private final SlugUtil slug;

    @Override
    @Transactional  // This will apply per-tenant transaction due to routing
    public void run(String... args) throws Exception {
        // Iterate over all tenants
        for (String tenantId : tenantProperties.getTenants().keySet()) {
            initializeForTenant(tenantId);
        }
        System.out.println("RBAC initialization completed for all tenants.");
    }

    private void initializeForTenant(String tenantId) {
        // Set the current tenant context to route to the correct database
        TenantContext.setTenantId(tenantId);

        try {
            // ✅ Check if tables already have data for this tenant
            if (permissionRepository.count() > 0 || roleRepository.count() > 0) {
                System.out.println("Permissions or roles already exist for tenant '" + tenantId + "'. Skipping RBAC initialization.");
                return;
            }

            // Step 1: Define permissions
            List<Permission> permissions = Arrays.asList(
                    new Permission(null, "CAN CREATE USER",slug.toSlug("CAN_CREATE_USER") ,"Can create users"),
                    new Permission(null, "CAN DELETE USER",slug.toSlug("CAN_DELETE_USER"), "Can delete users"),
                    new Permission(null, "CAN UPDATE USER",slug.toSlug("CAN_UPDATE_USER"), "Can update users"),
                    new Permission(null, "CAN VIEW USER",slug.toSlug("CAN_VIEW_USER"), "Can view users"),
                    new Permission(null, "CAN APPROVE CLAIM",slug.toSlug("CAN_APPROVE_CLAIM"), "Can approve insurance claim"),
                    new Permission(null, "CAN PROCESS CLAIM",slug.toSlug("CAN_PROCESS_CLAIM"), "Can process insurance claim"),
                    new Permission(null, "CAN MANAGE ROLES",slug.toSlug("CAN_MANAGE_ROLES"), "Can manage roles and permissions"),
                    new Permission(null, "CAN MANAGE FINANCE",slug.toSlug("CAN_MANAGE_FINANCE"), "Can manage financial transactions")
            );

            // Step 2: Save permissions and flush to get IDs
            permissionRepository.saveAllAndFlush(permissions);

            // Step 3: Map permissions by name for easy lookup
            Map<String, Permission> permMap = new HashMap<>();
            permissions.forEach(p -> permMap.put(p.getName(), p));

            // Step 4: Define roles with their permissions
            List<Role> roles = Arrays.asList(
                    new Role(null, "UNDERWRITER",slug.toSlug("UNDERWRITER"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_APPROVE_CLAIM")
                    ))),
                    new Role(null, "TELLER",slug.toSlug("TELLER"),  new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM")
                    ))),
                    new Role(null, "CLAIM PROCESSOR",slug.toSlug("CLAIM_PROCESSOR"),  new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM"),
                            permMap.get("CAN_APPROVE_CLAIM")
                    ))),
                    new Role(null, "ADMIN",slug.toSlug("ADMIN"),  new HashSet<>(Arrays.asList(
                            permMap.get("CAN_CREATE_USER"),
                            permMap.get("CAN_DELETE_USER"),
                            permMap.get("CAN_UPDATE_USER"),
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_ROLES"),
                            permMap.get("CAN_MANAGE_FINANCE")
                    ))),
                    new Role(null, "MANAGEMENT",slug.toSlug("MANAGEMENT"),  new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_FINANCE"),
                            permMap.get("CAN_MANAGE_ROLES")
                    )))
            );

            // Step 5: Save roles
            roleRepository.saveAll(roles);

            System.out.println("RBAC permissions and roles initialized successfully for tenant '" + tenantId + "'.");
        } finally {
            // Clear the tenant context to prevent interference with subsequent tenants or runtime requests
            TenantContext.clear();
        }
    }
}