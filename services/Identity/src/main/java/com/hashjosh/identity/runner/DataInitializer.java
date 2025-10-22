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

        // 1. Create Tenants
        Tenant govTenant = createTenant("GOVERNMENT", "Government Sector");
        Tenant farmerTenant = createTenant("FARMER", "Farmer Organization");
        Tenant insuranceTenant = createTenant("INSURANCE", "Insurance Provider");

        // 2. Create Permissions
        Map<String, Permission> permMap = createPermissions(Map.ofEntries(
            entry("VIEW_DASHBOARD", "Can view dashboard"),
            entry("MANAGE_USERS", "Can manage users"),
            entry("SUBMIT_APPLICATION", "Can submit applications"),
            entry("PROCESS_APPLICATION", "Can process applications"),
            entry("VIEW_REPORTS", "Can view reports"),
            entry("MANAGE_POLICIES", "Can manage insurance policies"),
            entry("SUBMIT_CLAIM", "Can submit insurance claims"),
            entry("PROCESS_CLAIM", "Can process insurance claims"),
            entry("MANAGE_PAYMENTS", "Can manage payments"),
            entry("ADMIN_ACCESS", "Has administrative access")
        ));

        // 3. Create Roles for each Tenant
        // Government Roles
        createRole(govTenant, "Government Admin", Set.of(
            permMap.get("ADMIN_ACCESS"),
            permMap.get("MANAGE_USERS"),
            permMap.get("VIEW_REPORTS")
        ), "/gov/admin/dashboard");

        createRole(govTenant, "Agriculture Officer", Set.of(
            permMap.get("VIEW_DASHBOARD"),
            permMap.get("PROCESS_APPLICATION"),
            permMap.get("VIEW_REPORTS")
        ), "/gov/officer/dashboard");

        // Farmer Roles
        createRole(farmerTenant, "Farmer", Set.of(
            permMap.get("VIEW_DASHBOARD"),
            permMap.get("SUBMIT_APPLICATION"),
            permMap.get("SUBMIT_CLAIM")
        ), "/farmer/dashboard");

        createRole(farmerTenant, "Farmer Group Leader", Set.of(
            permMap.get("VIEW_DASHBOARD"),
            permMap.get("SUBMIT_APPLICATION"),
            permMap.get("SUBMIT_CLAIM"),
            permMap.get("VIEW_REPORTS")
        ), "/farmer/leader/dashboard");

        // Insurance Provider Roles
        createRole(insuranceTenant, "Insurance Admin", Set.of(
            permMap.get("ADMIN_ACCESS"),
            permMap.get("MANAGE_USERS"),
            permMap.get("MANAGE_POLICIES"),
            permMap.get("VIEW_REPORTS")
        ), "/insurance/admin/dashboard");

        createRole(insuranceTenant, "Claims Officer", Set.of(
            permMap.get("VIEW_DASHBOARD"),
            permMap.get("PROCESS_CLAIM"),
            permMap.get("VIEW_REPORTS")
        ), "/insurance/claims/dashboard");

        createRole(insuranceTenant, "Policy Officer", Set.of(
            permMap.get("VIEW_DASHBOARD"),
            permMap.get("MANAGE_POLICIES"),
            permMap.get("PROCESS_APPLICATION")
        ), "/insurance/policy/dashboard");

        // 4. Create Sample Users
        createSampleUsers(govTenant, farmerTenant, insuranceTenant);

        log.info("✅ Data initialization completed successfully.");
    }

    private boolean isDataExists() {
        return tenantRepository.count() > 0 || 
               permissionRepository.count() > 0 || 
               roleRepository.count() > 0 || 
               userRepository.count() > 0;
    }

    private Tenant createTenant(String key, String name) {
        Tenant tenant = Tenant.builder()
                .key(key)
                .name(name)
                .roles(new ArrayList<>())
                .build();
        return tenantRepository.save(tenant);
    }

    private Map<String, Permission> createPermissions(Map<String, String> permissions) {
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

    private Role createRole(Tenant tenant, String name, Set<Permission> permissions, String defaultRoute) {
        Role role = Role.builder()
                .tenant(tenant)
                .name(name)
                .description("Role for " + name)
                .defaultRoute(defaultRoute)
                .permissions(permissions)
                .build();
        return roleRepository.save(role);
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

    private User createUser(String username, String email, Tenant tenant, Role role) {
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
        return userRepository.save(user);
    }

    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return Map.entry(key, value);
    }
}