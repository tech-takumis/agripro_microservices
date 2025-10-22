package com.hashjosh.identity.runner;

import com.hashjosh.identity.entity.*;
import com.hashjosh.identity.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Starting Data Initialization...");

        if (isDataExists()) {
            log.warn("⚠️ Skipping Data Initialization — existing data detected");
            return;
        }

        log.info("🆕 No existing data found — proceeding with initialization...");

        // 1️⃣ Create Tenants with Profile Fields
        Map<String, Tenant> tenants = createTenants();
        createTenantProfileFields(tenants);

        // 2️⃣ Create Permissions
        Map<String, Permission> permissions = createPermissions();

        // 3️⃣ Create Roles with Permissions
        createRoles(tenants, permissions);

        log.info("✅ Data initialization completed successfully.");
    }

    private boolean isDataExists() {
        return tenantRepository.count() > 0 ||
                permissionRepository.count() > 0 ||
                roleRepository.count() > 0 ||
                rolePermissionRepository.count() > 0;
    }

    private Map<String, Tenant> createTenants() {
        Map<String, Tenant> tenants = new HashMap<>();

        tenants.put("AGRICULTURE", createTenant("AGRICULTURE", "Department of Agriculture"));
        tenants.put("FARMER", createTenant("FARMER", "Farmer"));
        tenants.put("PCIC", createTenant("PCIC", "Philippine Crop Insurance Corporation"));

        return tenants;
    }

    private Tenant createTenant(String key, String name) {
        return tenantRepository.save(Tenant.builder()
                .key(key)
                .name(name)
                .build());
    }

    private void createTenantProfileFields(Map<String, Tenant> tenants) {
        // Agriculture Profile Fields
        createProfileFields(tenants.get("AGRICULTURE"), List.of(
                newProfileField("position", "Position", TenantProfileField.DataType.TEXT, true),
                newProfileField("office_location", "Office Location", TenantProfileField.DataType.TEXT, true)
        ));

        // Farmer Profile Fields
        createProfileFields(tenants.get("FARMER"), List.of(
                newProfileField("farm_size", "Farm Size (ha)", TenantProfileField.DataType.NUMBER, true),
                newProfileField("education", "Education", TenantProfileField.DataType.TEXT, false),
                newProfileField("farming_type", "Farming Type", TenantProfileField.DataType.TEXT, true)
        ));

        // PCIC Profile Fields
        createProfileFields(tenants.get("PCIC"), List.of(
                newProfileField("department", "Department", TenantProfileField.DataType.TEXT, true),
                newProfileField("position", "Position", TenantProfileField.DataType.TEXT, true)
        ));
    }

    private TenantProfileField newProfileField(String key, String label, TenantProfileField.DataType type, boolean required) {
        return TenantProfileField.builder()
                .fieldKey(key)
                .label(label)
                .dataType(type)
                .required(required)
                .build();
    }

    private void createProfileFields(Tenant tenant, List<TenantProfileField> fields) {
        fields.forEach(field -> {
            field.setTenant(tenant);
            if (!tenantProfileFieldRepository.existsByTenantAndFieldKey(tenant, field.getFieldKey())) {
                tenantProfileFieldRepository.save(field);
            }
        });
    }

    private Map<String, Permission> createPermissions() {
        Map<String, String> permissionDefs = new HashMap<>();

        // Add all permissions
        permissionDefs.put("CAN_SUBMIT_CROP_DATA", "Can submit crop data");
        permissionDefs.put("CAN_VIEW_REPORTS", "Can view report");
        permissionDefs.put("CAN_DEVELOP_PLANS", "Can develop agricultural plans");
        // ... add all other permissions from your original code

        return createPermissionEntities(permissionDefs);
    }

    private Map<String, Permission> createPermissionEntities(Map<String, String> permissionDefs) {
        Map<String, Permission> permissionMap = new HashMap<>();
        permissionDefs.forEach((name, description) -> {
            Permission permission = permissionRepository.findByName(name)
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .name(name)
                            .description(description)
                            .build()));
            permissionMap.put(name, permission);
        });
        return permissionMap;
    }

    private void createRoles(Map<String, Tenant> tenants, Map<String, Permission> permissions) {
        // Agriculture Roles
        createRole(tenants.get("AGRICULTURE"), "Municipal Agriculturist", Set.of(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_CONDUCT_BRIEFINGS"),
                permissions.get("CAN_INSPECT_FIELD")
        ), "/municipal-agriculturist/dashboard");

        createRole(tenants.get("AGRICULTURE"), "Agricultural Extension Worker", Set.of(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_FACILITATE_APPLICATIONS"),
                permissions.get("CAN_RECEIVE_CLAIMS_ON_SITE")
        ), "/agriculture/extension-worker/dashboard");

        // Farmer Roles
        createRole(tenants.get("FARMER"), "Farmer", Set.of(
                permissions.get("CAN_VIEW_POLICY"),
                permissions.get("CAN_FACILITATE_APPLICATIONS")
        ), "/farmer/dashboard");

        // PCIC Roles
        createRole(tenants.get("PCIC"), "UNDERWRITER", Set.of(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_ISSUE_POLICY"),
                permissions.get("CAN_ASSESS_RISK"),
                permissions.get("CAN_VIEW_POLICY"),
                permissions.get("CAN_COMPUTE_PREMIUM"),
                permissions.get("CAN_CONDUCT_BRIEFINGS"),
                permissions.get("CAN_INSPECT_FIELD")
        ), "/underwriter/dashboard");

        // ... add other PCIC roles from your original code
    }

    private Role createRole(Tenant tenant, String name, Set<Permission> permissions, String defaultRoute) {
        Role role = roleRepository.findByNameAndTenant(name, tenant)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(name)
                        .tenant(tenant)
                        .defaultRoute(defaultRoute)
                        .description("Role for " + name)
                        .build()));

        permissions.forEach(permission -> {
            if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                rolePermissionRepository.save(RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build());
            }
        });

        return role;
    }
}