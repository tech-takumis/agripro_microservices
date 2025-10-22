package com.hashjosh.identity.runner;

import com.hashjosh.identity.entity.Permission;
import com.hashjosh.identity.entity.Role;
import com.hashjosh.identity.entity.Tenant;
import com.hashjosh.identity.repository.PermissionRepository;
import com.hashjosh.identity.repository.RoleRepository;
import com.hashjosh.identity.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

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

        // 1️⃣ Ensure Tenants exist
        Map<String, Tenant> tenants = new HashMap<>();
        tenants.put("AGRICULTURE", upsertTenant("AGRICULTURE", "Department of Agriculture"));
        tenants.put("FARMER", upsertTenant("FARMER", "Farmer"));
        tenants.put("PCIC", upsertTenant("PCIC", "Philippine Crop Insurance Corporation"));

        // 2️⃣ Ensure Permissions exist
        Map<String, Permission> permissions = upsertPermissions(Map.ofEntries(
                Map.entry("CAN_SUBMIT_CROP_DATA", "Can submit crop data"),
                Map.entry("CAN_VIEW_REPORTS", "Can view report"),
                Map.entry("CAN_DEVELOP_PLANS", "Can develop agricultural plans"),
                Map.entry("CAN_CONDUCT_SURVEYS", "Can conduct agricultural surveys"),
                Map.entry("CAN_VIEW_USER", "Can view users"),
                Map.entry("CAN_ISSUE_POLICY", "Can issue policy"),
                Map.entry("CAN_ASSESS_RISK", "Can assess insurance risk"),
                Map.entry("CAN_VIEW_POLICY", "Can view policy"),
                Map.entry("CAN_COMPUTE_PREMIUM", "Can compute insurance premium"),
                Map.entry("CAN_CONDUCT_BRIEFINGS", "Can conduct farmer briefings"),
                Map.entry("CAN_INSPECT_FIELD", "Can inspect fields"),
                Map.entry("CAN_PROCESS_CLAIM", "Can process claims"),
                Map.entry("CAN_VERIFY_CLAIM", "Can verify claims"),
                Map.entry("CAN_ADJUST_CLAIMS", "Can adjust claims"),
                Map.entry("CAN_PROCESS_INDEMNITY", "Can process indemnity"),
                Map.entry("CAN_NOTIFY_DENIAL", "Can notify claim denial"),
                Map.entry("CAN_PROCESS_LEAVE", "Can process employee leave"),
                Map.entry("CAN_MANAGE_CASH_ADVANCE", "Can manage cash advances"),
                Map.entry("CAN_ISSUE_CERTIFICATE", "Can issue certificates"),
                Map.entry("CAN_MANAGE_PERSONNEL_RECORDS", "Can manage personnel records"),
                Map.entry("CAN_MANAGE_SUPPLIES", "Can manage supplies"),
                Map.entry("CAN_HANDLE_REPAIRS", "Can handle repair requests"),
                Map.entry("CAN_PROVIDE_TRANSPORT", "Can provide transport"),
                Map.entry("CAN_ISSUE_RECEIPT", "Can issue receipts"),
                Map.entry("CAN_FACILITATE_APPLICATIONS", "Can facilitate applications"),
                Map.entry("CAN_RECEIVE_CLAIMS_ON_SITE", "Can receive claims on site"),
                Map.entry("CAN_ENCODE_CLAIM", "Can encode claim data")
        ));

        // 3️⃣ Ensure Roles exist and have correct permissions
        upsertRole(tenants.get("AGRICULTURE"), "Municipal Agriculturist", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_CONDUCT_BRIEFINGS"),
                permissions.get("CAN_INSPECT_FIELD")
        )), "/municipal-agriculturist/dashboard");

        upsertRole(tenants.get("AGRICULTURE"), "Agricultural Extension Worker", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_FACILITATE_APPLICATIONS"),
                permissions.get("CAN_RECEIVE_CLAIMS_ON_SITE")
        )), "/agriculture/extension-worker/dashboard");

        upsertRole(tenants.get("FARMER"), "Farmer", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_POLICY"),
                permissions.get("CAN_FACILITATE_APPLICATIONS")
        )), "/farmer/dashboard");

        upsertRole(tenants.get("PCIC"), "UNDERWRITER", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_ISSUE_POLICY"),
                permissions.get("CAN_ASSESS_RISK"),
                permissions.get("CAN_VIEW_POLICY"),
                permissions.get("CAN_COMPUTE_PREMIUM"),
                permissions.get("CAN_CONDUCT_BRIEFINGS"),
                permissions.get("CAN_INSPECT_FIELD")
        )), "/underwriter/dashboard");

        upsertRole(tenants.get("PCIC"), "CLAIMS_ADJUSTMENT_STAFF", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_PROCESS_CLAIM"),
                permissions.get("CAN_VERIFY_CLAIM"),
                permissions.get("CAN_ADJUST_CLAIMS"),
                permissions.get("CAN_INSPECT_FIELD"),
                permissions.get("CAN_PROCESS_INDEMNITY"),
                permissions.get("CAN_NOTIFY_DENIAL")
        )), "/claims-adjustment-staff/dashboard");

        upsertRole(tenants.get("PCIC"), "ADMINISTRATIVE_STAFF", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_PROCESS_LEAVE"),
                permissions.get("CAN_MANAGE_CASH_ADVANCE"),
                permissions.get("CAN_ISSUE_CERTIFICATE"),
                permissions.get("CAN_MANAGE_PERSONNEL_RECORDS")
        )), "/administrative-staff/dashboard");

        upsertRole(tenants.get("PCIC"), "SUPPORT_STAFF", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_MANAGE_SUPPLIES"),
                permissions.get("CAN_HANDLE_REPAIRS"),
                permissions.get("CAN_PROVIDE_TRANSPORT"),
                permissions.get("CAN_ISSUE_RECEIPT")
        )), "/support-staff/dashboard");

        upsertRole(tenants.get("PCIC"), "EXTENSION_FIELD_STAFF", new HashSet<>(Arrays.asList(
                permissions.get("CAN_VIEW_USER"),
                permissions.get("CAN_FACILITATE_APPLICATIONS"),
                permissions.get("CAN_RECEIVE_CLAIMS_ON_SITE"),
                permissions.get("CAN_INSPECT_FIELD"),
                permissions.get("CAN_ENCODE_CLAIM")
        )), "/extension-field-staff/dashboard");

        log.info("✅ Data initialization completed successfully.");
    }

    private Tenant upsertTenant(String key, String name) {
        return tenantRepository.findByKey(key)
                .map(existing -> {
                    existing.setName(name);
                    return tenantRepository.save(existing);
                })
                .orElseGet(() -> tenantRepository.save(
                        Tenant.builder().key(key).name(name).build()
                ));
    }

    private Map<String, Permission> upsertPermissions(Map<String, String> permissionDefs) {
        Map<String, Permission> permissionMap = new HashMap<>();
        permissionDefs.forEach((name, description) -> {
            Permission permission = permissionRepository.findByName(name)
                    .map(existing -> {
                        existing.setDescription(description);
                        return permissionRepository.save(existing);
                    })
                    .orElseGet(() -> permissionRepository.save(
                            Permission.builder().name(name).description(description).build()
                    ));
            permissionMap.put(name, permission);
        });
        return permissionMap;
    }

    private Role upsertRole(Tenant tenant, String name, Set<Permission> permissions, String defaultRoute) {
        Role role = roleRepository.findByNameAndTenant(name, tenant)
                .map(existing -> {
                    existing.setDescription("Role for " + name);
                    existing.setDefaultRoute(defaultRoute);
                    existing.setPermissions(permissions);
                    return roleRepository.save(existing);
                })
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(name)
                                .tenant(tenant)
                                .description("Role for " + name)
                                .defaultRoute(defaultRoute)
                                .permissions(permissions)
                                .build()
                ));
        return role;
    }
}