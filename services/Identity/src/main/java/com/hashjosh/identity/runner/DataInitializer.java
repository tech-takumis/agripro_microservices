package com.hashjosh.identity.runner;

import com.hashjosh.identity.entity.*;
import com.hashjosh.identity.repository.*;
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
    private final TenantProfileFieldRepository tenantProfileFieldRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Starting Data Initialization...");

        // ====================================================
        // 🚨 Skip seeding if data already exists
        // ====================================================
        long tenantCount = tenantRepository.count();
        long permissionCount = permissionRepository.count();
        long roleCount = roleRepository.count();
        long rolePermissionCount = rolePermissionRepository.count();

        if (tenantCount > 0 || permissionCount > 0 || roleCount > 0 || rolePermissionCount > 0) {
            log.warn("⚠️ Skipping Data Initialization — existing data detected (tenants={}, permissions={}, roles={}, rolePermissions={})",
                    tenantCount, permissionCount, roleCount, rolePermissionCount);
            return;
        }

        log.info("🆕 No existing data found — proceeding with initialization...");

        // ====================================================
        // 1️⃣ Create Tenants
        // ====================================================
        Tenant agriculture = createTenantIfNotExist("AGRICULTURE", "Department of Agriculture");
        Tenant farmer = createTenantIfNotExist("FARMER", "Farmer");
        Tenant pcic = createTenantIfNotExist("PCIC", "Philippine Crop Insurance Corporation");

        // ====================================================
        // 2️⃣ Create Tenant Profile Fields
        // ====================================================
        createTenantProfileFields(agriculture, List.of(
                newField("position", "Position", TenantProfileField.DataType.TEXT, true),
                newField("office_location", "Office Location", TenantProfileField.DataType.TEXT, true)
        ));
        createTenantProfileFields(farmer, List.of(
                newField("farm_size", "Farm Size (ha)", TenantProfileField.DataType.NUMBER, true),
                newField("education", "Education", TenantProfileField.DataType.TEXT, false),
                newField("farming_type", "Farming Type", TenantProfileField.DataType.TEXT, true)
        ));
        createTenantProfileFields(pcic, List.of(
                newField("department", "Department", TenantProfileField.DataType.TEXT, true),
                newField("position", "Position", TenantProfileField.DataType.TEXT, true)
        ));

        // ====================================================
        // 3️⃣ Define Permissions
        // ====================================================
        Map<String, Permission> permMap = seedPermissions(Map.ofEntries(
                entry("CAN_SUBMIT_CROP_DATA", "Can submit crop data"),
                entry("CAN_VIEW_REPORTS", "Can view report"),
                entry("CAN_DEVELOP_PLANS", "Can develop agricultural plans"),
                entry("CAN_CONDUCT_SURVEYS", "Can conduct agricultural surveys"),
                entry("CAN_VIEW_USER", "Can view users"),
                entry("CAN_ISSUE_POLICY", "Can issue policy"),
                entry("CAN_ASSESS_RISK", "Can assess insurance risk"),
                entry("CAN_VIEW_POLICY", "Can view policy"),
                entry("CAN_COMPUTE_PREMIUM", "Can compute insurance premium"),
                entry("CAN_CONDUCT_BRIEFINGS", "Can conduct farmer briefings"),
                entry("CAN_INSPECT_FIELD", "Can inspect fields"),
                entry("CAN_PROCESS_CLAIM", "Can process claims"),
                entry("CAN_VERIFY_CLAIM", "Can verify claims"),
                entry("CAN_ADJUST_CLAIMS", "Can adjust claims"),
                entry("CAN_PROCESS_INDEMNITY", "Can process indemnity"),
                entry("CAN_NOTIFY_DENIAL", "Can notify claim denial"),
                entry("CAN_PROCESS_LEAVE", "Can process employee leave"),
                entry("CAN_MANAGE_CASH_ADVANCE", "Can manage cash advances"),
                entry("CAN_ISSUE_CERTIFICATE", "Can issue certificates"),
                entry("CAN_MANAGE_PERSONNEL_RECORDS", "Can manage personnel records"),
                entry("CAN_MANAGE_SUPPLIES", "Can manage supplies"),
                entry("CAN_HANDLE_REPAIRS", "Can handle repair requests"),
                entry("CAN_PROVIDE_TRANSPORT", "Can provide transport"),
                entry("CAN_ISSUE_RECEIPT", "Can issue receipts"),
                entry("CAN_FACILITATE_APPLICATIONS", "Can facilitate applications"),
                entry("CAN_RECEIVE_CLAIMS_ON_SITE", "Can receive claims on site"),
                entry("CAN_ENCODE_CLAIM", "Can encode claim data")
        ));

        // ====================================================
        // 4️⃣ Create Roles per Tenant
        // ====================================================

        // 🏢 AGRICULTURE Tenant Roles
        createRole(agriculture, "Municipal Agriculturist", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_CONDUCT_BRIEFINGS"),
                permMap.get("CAN_INSPECT_FIELD")
        ), "/municipal-agriculturist/dashboard");

        createRole(agriculture, "Agricultural Extension Worker", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_FACILITATE_APPLICATIONS"),
                permMap.get("CAN_RECEIVE_CLAIMS_ON_SITE")
        ), "/agriculture/extension-worker/dashboard");

        // 👨‍🌾 FARMER Tenant Role
        createRole(farmer, "Farmer", Set.of(
                permMap.get("CAN_VIEW_POLICY"),
                permMap.get("CAN_FACILITATE_APPLICATIONS")
        ), "/farmer/dashboard");

        // 🏦 PCIC Tenant Roles
        createRole(pcic, "UNDERWRITER", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_ISSUE_POLICY"),
                permMap.get("CAN_ASSESS_RISK"),
                permMap.get("CAN_VIEW_POLICY"),
                permMap.get("CAN_COMPUTE_PREMIUM"),
                permMap.get("CAN_CONDUCT_BRIEFINGS"),
                permMap.get("CAN_INSPECT_FIELD")
        ), "/underwriter/dashboard");

        createRole(pcic, "CLAIMS_ADJUSTMENT_STAFF", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_PROCESS_CLAIM"),
                permMap.get("CAN_VERIFY_CLAIM"),
                permMap.get("CAN_ADJUST_CLAIMS"),
                permMap.get("CAN_INSPECT_FIELD"),
                permMap.get("CAN_PROCESS_INDEMNITY"),
                permMap.get("CAN_NOTIFY_DENIAL")
        ), "/claims-adjustment-staff/dashboard");

        createRole(pcic, "ADMINISTRATIVE_STAFF", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_PROCESS_LEAVE"),
                permMap.get("CAN_MANAGE_CASH_ADVANCE"),
                permMap.get("CAN_ISSUE_CERTIFICATE"),
                permMap.get("CAN_MANAGE_PERSONNEL_RECORDS")
        ), "/administrative-staff/dashboard");

        createRole(pcic, "SUPPORT_STAFF", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_MANAGE_SUPPLIES"),
                permMap.get("CAN_HANDLE_REPAIRS"),
                permMap.get("CAN_PROVIDE_TRANSPORT"),
                permMap.get("CAN_ISSUE_RECEIPT")
        ), "/support-staff/dashboard");

        createRole(pcic, "EXTENSION_FIELD_STAFF", Set.of(
                permMap.get("CAN_VIEW_USER"),
                permMap.get("CAN_FACILITATE_APPLICATIONS"),
                permMap.get("CAN_RECEIVE_CLAIMS_ON_SITE"),
                permMap.get("CAN_INSPECT_FIELD"),
                permMap.get("CAN_ENCODE_CLAIM")
        ), "/extension-field-staff/dashboard");

        log.info("✅ Data initialization completed.");
    }

    // ====================================================
    // 🔧 Helper Methods
    // ====================================================

    private Tenant createTenantIfNotExist(String key, String name) {
        return tenantRepository.findByKeyIgnoreCase(key)
                .orElseGet(() -> {
                    Tenant t = new Tenant();
                    t.setKey(key);
                    t.setName(name);
                    return tenantRepository.save(t);
                });
    }

    private TenantProfileField newField(String key, String label, TenantProfileField.DataType type, boolean required) {
        TenantProfileField f = new TenantProfileField();
        f.setFieldKey(key);
        f.setLabel(label);
        f.setDataType(type);
        f.setRequired(required);
        return f;
    }

    private void createTenantProfileFields(Tenant tenant, List<TenantProfileField> fields) {
        for (TenantProfileField f : fields) {
            f.setTenant(tenant);
            if (!tenantProfileFieldRepository.existsByTenantAndFieldKey(tenant, f.getFieldKey())) {
                tenantProfileFieldRepository.save(f);
            }
        }
    }

    private Map<String, Permission> seedPermissions(Map<String, String> permissions) {
        Map<String, Permission> map = new HashMap<>();
        for (var entry : permissions.entrySet()) {
            String key = entry.getKey();
            Permission p = permissionRepository.findByName(key).orElseGet(() -> {
                Permission perm = new Permission();
                perm.setName(key);
                perm.setDescription(entry.getValue());
                return permissionRepository.save(perm);
            });
            map.put(key, p);
        }
        return map;
    }

    private void createRole(Tenant tenant, String name, Set<Permission> permissions, String defaultRoute) {
        Role role = roleRepository.findByNameAndTenant(name, tenant).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            r.setTenant(tenant);
            r.setDefaultRoute(defaultRoute);
            r.setDescription(name);
            return roleRepository.save(r);
        });

        for (Permission p : permissions) {
            if (!rolePermissionRepository.existsByRoleAndPermission(role, p)) {
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            }
        }
    }

    private static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return Map.entry(k, v);
    }
}
