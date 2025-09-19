package com.hashjosh.users.runner;

import com.hashjosh.jwtshareable.service.TenantContext;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.users.entity.Permission;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.repository.PermissionRepository;
import com.hashjosh.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Run first
public class AgricultureRbacInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SlugUtil slug;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting RBAC initialization for tenant 'agriculture'");
        initializeForTenant("agriculture");
    }

    private boolean isRoleNotNull() {
        String tenantId = TenantContext.getTenantId();
        log.info("Checking roles/permissions for tenant '{}', context: {}", tenantId, tenantId);
        long permissionCount = permissionRepository.count();
        long roleCount = roleRepository.count();
        log.info("Permission count: {}, Role count: {}", permissionCount, roleCount);
        return permissionCount > 0 || roleCount > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // New transaction for isolation
    public void initializeForTenant(String tenantId) {
        TenantContext.setTenantId(tenantId);
        log.info("Set tenant context to '{}'", tenantId);
        try {
            if (isRoleNotNull()) {
                log.info("Permissions or roles already exist for tenant '{}'. Skipping RBAC initialization.", tenantId);
                return;
            }

            // Define permissions
            List<Permission> permissions = Arrays.asList(
                    new Permission(null, "CAN_CREATE_USER", slug.toSlug("CAN_CREATE_USER"), "Can create users"),
                    new Permission(null, "CAN_DELETE_USER", slug.toSlug("CAN_DELETE_USER"), "Can delete users"),
                    new Permission(null, "CAN_UPDATE_USER", slug.toSlug("CAN_UPDATE_USER"), "Can update users"),
                    new Permission(null, "CAN_VIEW_USER", slug.toSlug("CAN_VIEW_USER"), "Can view user details"),
                    new Permission(null, "CAN_PROCESS_CLAIM", slug.toSlug("CAN_PROCESS_CLAIM"), "Can process insurance claims for farmers"),
                    new Permission(null, "CAN_MANAGE_ROLES", slug.toSlug("CAN_MANAGE_ROLES"), "Can manage roles and permissions"),
                    new Permission(null, "CAN_SUBMIT_CROP_DATA", slug.toSlug("CAN_SUBMIT_CROP_DATA"), "Can submit crop-related data such as yield or damage reports"),
                    new Permission(null, "CAN_VIEW_REPORTS", slug.toSlug("CAN_VIEW_REPORTS"), "Can view agricultural reports and summaries"),
                    new Permission(null, "CAN_DEVELOP_PLANS", slug.toSlug("CAN_DEVELOP_PLANS"), "Can develop and implement agricultural plans, strategies, and policies for municipal approval"),
                    new Permission(null, "CAN_COORDINATE_AGENCIES", slug.toSlug("CAN_COORDINATE_AGENCIES"), "Can coordinate with government agencies, NGOs, and stakeholders to promote agricultural productivity"),
                    new Permission(null, "CAN_PROVIDE_INFRASTRUCTURE", slug.toSlug("CAN_PROVIDE_INFRASTRUCTURE"), "Can provision agricultural infrastructure, farm machinery, post-harvest facilities, and inputs to farmers"),
                    new Permission(null, "CAN_PROVIDE_TECHNICAL_ADVICE", slug.toSlug("CAN_PROVIDE_TECHNICAL_ADVICE"), "Can provide technical advice to farmers on crop calendars, fertilizers, pesticides, and organic standards"),
                    new Permission(null, "CAN_MONITOR_PROGRAMS", slug.toSlug("CAN_MONITOR_PROGRAMS"), "Can monitor and evaluate agricultural program implementation, including organic plans"),
                    new Permission(null, "CAN_CONDUCT_SURVEYS", slug.toSlug("CAN_CONDUCT_SURVEYS"), "Can conduct surveys for classifying agricultural areas and assessing needs"),
                    new Permission(null, "CAN_CONDUCT_TRAINING", slug.toSlug("CAN_CONDUCT_TRAINING"), "Can conduct educational training and workshops for farmers on modern practices and technologies"),
                    new Permission(null, "CAN_FACILITATE_TECH_ADOPTION", slug.toSlug("CAN_FACILITATE_TECH_ADOPTION"), "Can facilitate adoption of innovations, such as improved seeds, tools, or pest management"),
                    new Permission(null, "CAN_PROVIDE_DIAGNOSTIC_SERVICES", slug.toSlug("CAN_PROVIDE_DIAGNOSTIC_SERVICES"), "Can provide diagnostic services like soil testing, water quality analysis, or disease identification"),
                    new Permission(null, "CAN_ENHANCE_MARKET_ACCESS", slug.toSlug("CAN_ENHANCE_MARKET_ACCESS"), "Can assist farmers in accessing markets, meeting quality standards, and linking to value chains"),
                    new Permission(null, "CAN_PROMOTE_SUSTAINABILITY", slug.toSlug("CAN_PROMOTE_SUSTAINABILITY"), "Can promote sustainable practices, including erosion control, resource conservation, and community self-reliance"),
                    new Permission(null, "CAN_SUPPORT_VETERINARY", slug.toSlug("CAN_SUPPORT_VETERINARY"), "Can support livestock health, including assisting with animal disease treatment and farmer guidance")
            );

            permissionRepository.saveAllAndFlush(permissions);
            log.info("Saved {} permissions for tenant '{}'", permissions.size(), tenantId);

            Map<String, Permission> permMap = new HashMap<>();
            permissions.forEach(p -> permMap.put(p.getName(), p));

            // Define roles
            List<Role> roles = Arrays.asList(
                    new Role(null, "Municipal Agriculturists", slug.toSlug("Municipal Agriculturists"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM"),
                            permMap.get("CAN_SUBMIT_CROP_DATA"),
                            permMap.get("CAN_VIEW_REPORTS"),
                            permMap.get("CAN_DEVELOP_PLANS"),
                            permMap.get("CAN_COORDINATE_AGENCIES"),
                            permMap.get("CAN_PROVIDE_INFRASTRUCTURE"),
                            permMap.get("CAN_PROVIDE_TECHNICAL_ADVICE"),
                            permMap.get("CAN_MONITOR_PROGRAMS"),
                            permMap.get("CAN_CONDUCT_SURVEYS")
                    ))),
                    new Role(null, "Agricultural Extension Workers", slug.toSlug("Agricultural Extension Workers"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM"),
                            permMap.get("CAN_SUBMIT_CROP_DATA"),
                            permMap.get("CAN_CONDUCT_TRAINING"),
                            permMap.get("CAN_FACILITATE_TECH_ADOPTION"),
                            permMap.get("CAN_PROVIDE_DIAGNOSTIC_SERVICES"),
                            permMap.get("CAN_ENHANCE_MARKET_ACCESS"),
                            permMap.get("CAN_PROMOTE_SUSTAINABILITY"),
                            permMap.get("CAN_SUPPORT_VETERINARY")
                    ))),
                    new Role(null, "ADMIN", slug.toSlug("ADMIN"), new HashSet<>(Arrays.asList(
                            permMap.get("CAN_CREATE_USER"),
                            permMap.get("CAN_DELETE_USER"),
                            permMap.get("CAN_UPDATE_USER"),
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_ROLES")
                    )))
            );

            roleRepository.saveAllAndFlush(roles);
            log.info("Saved {} roles for tenant '{}'", roles.size(), tenantId);

            log.info("RBAC permissions and roles initialized successfully for tenant '{}'", tenantId);
        } catch (Exception e) {
            log.error("Failed to initialize RBAC for tenant '{}': {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("RBAC initialization failed for tenant '" + tenantId + "'", e);
        } finally {
            TenantContext.clear();
            log.info("Cleared tenant context for '{}'", tenantId);
        }
    }
}