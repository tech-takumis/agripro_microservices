package com.hashjosh.users.runner;

import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.users.entity.Permission;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.repository.PermissionRepository;
import com.hashjosh.users.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PcicRbacInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SlugUtil slug;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeForTenant("pcic");
    }

    private void initializeForTenant(String tenantId) {
        TenantContext.setTenantId(tenantId);
        try {
            if (permissionRepository.count() > 0 || roleRepository.count() > 0) {
                System.out.println("Permissions or roles already exist for tenant '" + tenantId + "'. Skipping RBAC initialization.");
                return;
            }

            // Define permissions (customize if needed)
            List<Permission> permissions = Arrays.asList(
                    new Permission(null, "CAN_CREATE_USER", slug.toSlug("CAN_CREATE_USER"), "Can create users"),
                    new Permission(null, "CAN_DELETE_USER", slug.toSlug("CAN_DELETE_USER"), "Can delete users"),
                    new Permission(null, "CAN_UPDATE_USER", slug.toSlug("CAN_UPDATE_USER"), "Can update users"),
                    new Permission(null, "CAN_VIEW_USER", slug.toSlug("CAN_VIEW_USER"), "Can view users"),
                    new Permission(null, "CAN_APPROVE_CLAIM", slug.toSlug("CAN_APPROVE_CLAIM"), "Can approve insurance claim"),
                    new Permission(null, "CAN_PROCESS_CLAIM", slug.toSlug("CAN_PROCESS_CLAIM"), "Can process insurance claim"),
                    new Permission(null, "CAN_MANAGE_ROLES", slug.toSlug("CAN_MANAGE_ROLES"), "Can manage roles and permissions"),
                    new Permission(null, "CAN_MANAGE_FINANCE", slug.toSlug("CAN_MANAGE_FINANCE"), "Can manage financial transactions")
            );

            permissionRepository.saveAllAndFlush(permissions);

            Map<String, Permission> permMap = new HashMap<>();
            permissions.forEach(p -> permMap.put(p.getName(), p));

            // Define roles (customize if needed)
            List<Role> roles = Arrays.asList(
                    new Role(null, "UNDERWRITER", slug.toSlug("UNDERWRITER"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_APPROVE_CLAIM")
                    ))),
                    new Role(null, "TELLER", slug.toSlug("TELLER"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM")
                    ))),
                    new Role(null, "CLAIM_PROCESSOR", slug.toSlug("CLAIM_PROCESSOR"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM"),
                            permMap.get("CAN_APPROVE_CLAIM")
                    ))),
                    new Role(null, "ADMIN", slug.toSlug("ADMIN"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_CREATE_USER"),
                            permMap.get("CAN_DELETE_USER"),
                            permMap.get("CAN_UPDATE_USER"),
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_ROLES"),
                            permMap.get("CAN_MANAGE_FINANCE")
                    ))),
                    new Role(null, "MANAGEMENT", slug.toSlug("MANAGEMENT"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_FINANCE"),
                            permMap.get("CAN_MANAGE_ROLES")
                    )))
            );

            roleRepository.saveAllAndFlush(roles);

            System.out.println("RBAC permissions and roles initialized successfully for tenant '" + tenantId + "'.");
        } catch (Exception e) {
            System.err.println("Failed to initialize RBAC for tenant '" + tenantId + "': " + e.getMessage());
            throw new RuntimeException("RBAC initialization failed for tenant '" + tenantId + "'", e);
        } finally {
            TenantContext.clear();
        }
    }
}