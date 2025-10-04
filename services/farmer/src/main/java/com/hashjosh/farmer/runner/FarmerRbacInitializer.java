package com.hashjosh.farmer.runner;

import com.hashjosh.farmer.entity.Permission;
import com.hashjosh.farmer.entity.Role;
import com.hashjosh.farmer.repository.PermissionRepository;
import com.hashjosh.farmer.repository.RoleRepository;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Order(3)
@Slf4j
public class FarmerRbacInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SlugUtil slug;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting RBAC initialization for tenant 'farmer'");
        try {
            // Check if data exists
            if (isRoleNotNull()) {
                log.info("Permissions or roles already exist for farmer. Skipping RBAC initialization.");
                return;
            }

            // Define permissions - Core user + Farmer-specific
            List<Permission> permissions = Arrays.asList(
                    // Basic User Permissions (limited to self)
                    new Permission(null, "CAN_VIEW_USER", slug.toSlug("CAN_VIEW_USER"), "Can view own user details"),
                    new Permission(null, "CAN_UPDATE_OWN_PROFILE", slug.toSlug("CAN_UPDATE_OWN_PROFILE"), "Can update their own user profile and contact information"),

                    // Claim Permissions (self-service only)
                    new Permission(null, "CAN_SUBMIT_CLAIM", slug.toSlug("CAN_SUBMIT_CLAIM"), "Can submit insurance or subsidy claims for their own farm"),

                    // Farm Data and Activities
                    new Permission(null, "CAN_SUBMIT_CROP_DATA", slug.toSlug("CAN_SUBMIT_CROP_DATA"), "Can submit crop-related data such as yield or damage reports"),
                    new Permission(null, "CAN_VIEW_OWN_FARM_DATA", slug.toSlug("CAN_VIEW_OWN_FARM_DATA"), "Can view their own historical farm data, yields, and analytics"),
                    new Permission(null, "CAN_LOG_FARM_ACTIVITIES", slug.toSlug("CAN_LOG_FARM_ACTIVITIES"), "Can log daily activities like planting, fertilizing, or harvesting"),

                    // Insights and Market
                    new Permission(null, "CAN_ACCESS_MARKET_INFO", slug.toSlug("CAN_ACCESS_MARKET_INFO"), "Can access market prices, buyer connections, and sales listings"),
                    new Permission(null, "CAN_RECEIVE_ADVISORIES", slug.toSlug("CAN_RECEIVE_ADVISORIES"), "Can receive personalized agronomic advice, weather forecasts, and alerts"),

                    // Compliance and Export
                    new Permission(null, "CAN_SUBMIT_COMPLIANCE_REPORTS", slug.toSlug("CAN_SUBMIT_COMPLIANCE_REPORTS"), "Can submit reports for regulatory compliance or sustainability tracking"),
                    new Permission(null, "CAN_EXPORT_OWN_DATA", slug.toSlug("CAN_EXPORT_OWN_DATA"), "Can export their own farm data in standard formats for personal use")
            );

            // Save permissions
            permissionRepository.saveAllAndFlush(permissions);
            log.info("Saved {} permissions for tenant ", permissions.size());

            // Map permissions by name
            Map<String, Permission> permMap = new HashMap<>();
            permissions.forEach(p -> permMap.put(p.getName(), p));

            // Define roles - Only FARMER role with appropriate permissions
            List<Role> roles = List.of(
                    new Role(null, "FARMER", slug.toSlug("FARMER"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_UPDATE_OWN_PROFILE"),
                            permMap.get("CAN_SUBMIT_CLAIM"),
                            permMap.get("CAN_SUBMIT_CROP_DATA"),
                            permMap.get("CAN_VIEW_OWN_FARM_DATA"),
                            permMap.get("CAN_LOG_FARM_ACTIVITIES"),
                            permMap.get("CAN_ACCESS_MARKET_INFO"),
                            permMap.get("CAN_RECEIVE_ADVISORIES"),
                            permMap.get("CAN_SUBMIT_COMPLIANCE_REPORTS"),
                            permMap.get("CAN_EXPORT_OWN_DATA")
                    )))
            );

            // Save roles
            roleRepository.saveAllAndFlush(roles);
            log.info("Saved {} roles for tenant ", roles.size());

            log.info("RBAC permissions and roles initialized successfully for tenant ");
        } catch (Exception e) {
            log.error("Failed to initialize RBAC for tenant '{}':", e.getMessage(), e);
            throw new RuntimeException("RBAC initialization failed for tenant '" , e);
        }
    }

    private boolean isRoleNotNull() {

        long permissionCount = permissionRepository.count();
        long roleCount = roleRepository.count();
        log.info("Permission count: {}, Role count: {}", permissionCount, roleCount);
        return permissionCount > 0 || roleCount > 0;
    }
}