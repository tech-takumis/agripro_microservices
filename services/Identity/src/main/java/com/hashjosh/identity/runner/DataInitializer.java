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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Starting Data Initialization...");

        if (isDataExists()) {
            log.warn("⚠️ Skipping Data Initialization — existing data detected");
            return;
        }

        log.info("🆕 No existing data found — proceeding with initialization...");

        // 1️⃣ Create Tenants
        Tenant agriculture = createTenantIfNotExist("AGRICULTURE", "Department of Agriculture");
        Tenant farmer = createTenantIfNotExist("FARMER", "Farmer");
        Tenant pcic = createTenantIfNotExist("PCIC", "Philippine Crop Insurance Corporation");

        // 2️⃣ Create Tenant Profile Fields (placeholder, implement if you have the entity/method)
        // createTenantProfileFields(agriculture, List.of(
        //         newField("position", "Position", TenantProfileField.DataType.TEXT, true),
        //         newField("office_location", "Office Location", TenantProfileField.DataType.TEXT, true)
        // ));
        // createTenantProfileFields(farmer, List.of(
        //         newField("farm_size", "Farm Size (ha)", TenantProfileField.DataType.NUMBER, true),
        //         newField("education", "Education", TenantProfileField.DataType.TEXT, false),
        //         newField("farming_type", "Farming Type", TenantProfileField.DataType.TEXT, true)
        // ));
        // createTenantProfileFields(pcic, List.of(
        //         newField("department", "Department", TenantProfileField.DataType.TEXT, true),
        //         newField("position", "Position", TenantProfileField.DataType.TEXT, true)
        // ));

        // 3️⃣ Define Permissions
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

        // 4️⃣ Create Roles per Tenant

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

    private boolean isDataExists() {
        return tenantRepository.count() > 0 ||
                permissionRepository.count() > 0 ||
                roleRepository.count() > 0 ||
                userRepository.count() > 0;
    }

    private Tenant createTenantIfNotExist(String key, String name) {
        return tenantRepository.findByKey(key)
                .orElseGet(() -> tenantRepository.save(
                        Tenant.builder()
                                .key(key)
                                .name(name)
                                .roles(new ArrayList<>())
                                .build()
                ));
    }

    private Map<String, Permission> seedPermissions(Map<String, String> permissions) {
        Map<String, Permission> permissionMap = new HashMap<>();
        permissions.forEach((name, description) -> {
            Permission permission = Permission.builder()
                    .name(name)
                    .description(description)
                    .build();
            permissionMap.put(name, permissionRepository.save(permission));
        });
        return permissionMap;
    }

    private void createRole(Tenant tenant, String name, Set<Permission> permissions, String defaultRoute) {
        Role role = Role.builder()
                .tenant(tenant)
                .name(name)
                .description("Role for " + name)
                .defaultRoute(defaultRoute)
                .permissions(new HashSet<>(permissions))
                .build();
        roleRepository.save(role);
    }

    private void createSampleUsers(Tenant govTenant, Tenant farmerTenant, Tenant insuranceTenant) {
        // Create sample users for each tenant
        createUser("gov.admin", "admin@gov.example.com", govTenant,
            roleRepository.findByNameAndTenant("Government Admin", govTenant).orElseThrow());

        createUser("farmer.user", "farmer@example.com", farmerTenant,
            roleRepository.findByNameAndTenant("Farmer", farmerTenant).orElseThrow());

        createUser("insurance.admin", "admin@insurance.example.com", insuranceTenant,
            roleRepository.findByNameAndTenant("Insurance Admin", insuranceTenant).orElseThrow());
    }

    private void createUser(String username, String email, Tenant tenant, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // encrypted "password"
                .firstName("Sample")
                .lastName("User")
                .emailVerified(true)
                .active(true)
                .tenant(tenant)
                .roles(Set.of(role))
                .permissions(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }

    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return Map.entry(key, value);
    }
}