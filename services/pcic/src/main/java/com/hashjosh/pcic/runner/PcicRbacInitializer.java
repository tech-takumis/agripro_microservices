package com.hashjosh.pcic.runner;

import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.pcic.entity.Permission;
import com.hashjosh.pcic.entity.Role;
import com.hashjosh.pcic.repository.PermissionRepository;
import com.hashjosh.pcic.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PcicRbacInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final SlugUtil slug;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting RBAC initialization for tenant 'pcic'");
        try {
            if (isRoleNotNull()) {
                log.info("Permissions or roles already exist for pcic. Skipping RBAC initialization.");
                return;
            }

            // Define permissions
            List<Permission> permissions = Arrays.asList(
                    new Permission(null, "CAN_CREATE_USER", slug.toSlug("CAN_CREATE_USER"), "Can create users"),
                    new Permission(null, "CAN_DELETE_USER", slug.toSlug("CAN_DELETE_USER"), "Can delete users"),
                    new Permission(null, "CAN_UPDATE_USER", slug.toSlug("CAN_UPDATE_USER"), "Can update users"),
                    new Permission(null, "CAN_VIEW_USER", slug.toSlug("CAN_VIEW_USER"), "Can view user details"),
                    new Permission(null, "CAN_PROCESS_CLAIM", slug.toSlug("CAN_PROCESS_CLAIM"), "Can process insurance claims"),
                    new Permission(null, "CAN_APPROVE_CLAIM", slug.toSlug("CAN_APPROVE_CLAIM"), "Can approve insurance claims"),
                    new Permission(null, "CAN_VERIFY_CLAIM", slug.toSlug("CAN_VERIFY_CLAIM"), "Can verify claim documents and coverage"),
                    new Permission(null, "CAN_ENCODE_CLAIM", slug.toSlug("CAN_ENCODE_CLAIM"), "Can encode claim data in the system"),
                    new Permission(null, "CAN_NOTIFY_DENIAL", slug.toSlug("CAN_NOTIFY_DENIAL"), "Can notify claimants of claim denials"),
                    new Permission(null, "CAN_ISSUE_POLICY", slug.toSlug("CAN_ISSUE_POLICY"), "Can issue crop insurance policies"),
                    new Permission(null, "CAN_ASSESS_RISK", slug.toSlug("CAN_ASSESS_RISK"), "Can assess risks for policy underwriting"),
                    new Permission(null, "CAN_VIEW_POLICY", slug.toSlug("CAN_VIEW_POLICY"), "Can view insurance policies"),
                    new Permission(null, "CAN_COMPUTE_PREMIUM", slug.toSlug("CAN_COMPUTE_PREMIUM"), "Can compute insurance premiums"),
                    new Permission(null, "CAN_CONDUCT_BRIEFINGS", slug.toSlug("CAN_CONDUCT_BRIEFINGS"), "Can conduct briefings on insurance programs"),
                    new Permission(null, "CAN_ADJUST_CLAIMS", slug.toSlug("CAN_ADJUST_CLAIMS"), "Can adjust claims and compute indemnity"),
                    new Permission(null, "CAN_INSPECT_FIELD", slug.toSlug("CAN_INSPECT_FIELD"), "Can conduct field inspections for claims and applications"),
                    new Permission(null, "CAN_PROCESS_INDEMNITY", slug.toSlug("CAN_PROCESS_INDEMNITY"), "Can process indemnity payments"),
                    new Permission(null, "CAN_MANAGE_ROLES", slug.toSlug("CAN_MANAGE_ROLES"), "Can manage roles and permissions"),
                    new Permission(null, "CAN_MANAGE_FINANCE", slug.toSlug("CAN_MANAGE_FINANCE"), "Can manage financial transactions"),
                    new Permission(null, "CAN_PROCESS_PAYMENT", slug.toSlug("CAN_PROCESS_PAYMENT"), "Can process premium payments and refunds"),
                    new Permission(null, "CAN_ISSUE_RECEIPT", slug.toSlug("CAN_ISSUE_RECEIPT"), "Can issue official receipts"),
                    new Permission(null, "CAN_PREPARE_CHEQUE", slug.toSlug("CAN_PREPARE_CHEQUE"), "Can prepare cheques for claim payouts"),
                    new Permission(null, "CAN_PREPARE_DV", slug.toSlug("CAN_PREPARE_DV"), "Can prepare disbursement vouchers"),
                    new Permission(null, "CAN_APPROVE_DV", slug.toSlug("CAN_APPROVE_DV"), "Can approve disbursement vouchers"),
                    new Permission(null, "CAN_PROCESS_LEAVE", slug.toSlug("CAN_PROCESS_LEAVE"), "Can process leave applications"),
                    new Permission(null, "CAN_MANAGE_CASH_ADVANCE", slug.toSlug("CAN_MANAGE_CASH_ADVANCE"), "Can manage cash advances and liquidations"),
                    new Permission(null, "CAN_ISSUE_CERTIFICATE", slug.toSlug("CAN_ISSUE_CERTIFICATE"), "Can issue certificates of employment and service records"),
                    new Permission(null, "CAN_MANAGE_PERSONNEL_RECORDS", slug.toSlug("CAN_MANAGE_PERSONNEL_RECORDS"), "Can manage personnel records and separations"),
                    new Permission(null, "CAN_MANAGE_SUPPLIES", slug.toSlug("CAN_MANAGE_SUPPLIES"), "Can manage requisitioning and issuance of supplies"),
                    new Permission(null, "CAN_HANDLE_REPAIRS", slug.toSlug("CAN_HANDLE_REPAIRS"), "Can handle repairs and maintenance of assets"),
                    new Permission(null, "CAN_PROVIDE_TRANSPORT", slug.toSlug("CAN_PROVIDE_TRANSPORT"), "Can provide transportation and driver services"),
                    new Permission(null, "CAN_FACILITATE_APPLICATIONS", slug.toSlug("CAN_FACILITATE_APPLICATIONS"), "Can facilitate insurance applications at field level"),
                    new Permission(null, "CAN_RECEIVE_CLAIMS_ON_SITE", slug.toSlug("CAN_RECEIVE_CLAIMS_ON_SITE"), "Can receive and initial process claims on-site"),
                    new Permission(null, "CAN_OVERSIGHT_CLAIMS", slug.toSlug("CAN_OVERSIGHT_CLAIMS"), "Can oversee and review claims processing")
            );

            permissionRepository.saveAllAndFlush(permissions);
            log.info("Saved {} permissions for pcic", permissions.size());

            Map<String, Permission> permMap = new HashMap<>();
            permissions.forEach(p -> permMap.put(p.getName(), p));

            // Define roles
            List<Role> roles = Arrays.asList(
                    new Role(null, "UNDERWRITER", slug.toSlug("UNDERWRITER"),"/underwriter/dashboard", new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_ISSUE_POLICY"),
                            permMap.get("CAN_ASSESS_RISK"),
                            permMap.get("CAN_VIEW_POLICY"),
                            permMap.get("CAN_COMPUTE_PREMIUM"),
                            permMap.get("CAN_CONDUCT_BRIEFINGS"),
                            permMap.get("CAN_INSPECT_FIELD")
                    ))),
                    new Role(null, "ADMIN", slug.toSlug("ADMIN"),"/admin/dashboard", new HashSet<>(Arrays.asList(
                            permMap.get("CAN_CREATE_USER"),
                            permMap.get("CAN_DELETE_USER"),
                            permMap.get("CAN_UPDATE_USER"),
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_ROLES"),
                            permMap.get("CAN_MANAGE_FINANCE"),
                            permMap.get("CAN_APPROVE_DV"),
                            permMap.get("CAN_OVERSIGHT_CLAIMS")
                    ))),
                    new Role(null, "CLAIMS_ADJUSTMENT_STAFF", slug.toSlug("CLAIMS_ADJUSTMENT_STAFF"),"/claims-processor/dashboard", new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_PROCESS_CLAIM"),
                            permMap.get("CAN_VERIFY_CLAIM"),
                            permMap.get("CAN_ADJUST_CLAIMS"),
                            permMap.get("CAN_INSPECT_FIELD"),
                            permMap.get("CAN_PROCESS_INDEMNITY"),
                            permMap.get("CAN_NOTIFY_DENIAL")
                    ))),
                    new Role(null, "TELLER_STAFF", slug.toSlug("TELLER_STAFF"), "/teller/dashboard",new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_MANAGE_SUPPLIES"),
                            permMap.get("CAN_HANDLE_REPAIRS"),
                            permMap.get("CAN_PROVIDE_TRANSPORT"),
                            permMap.get("CAN_ISSUE_RECEIPT")
                    ))),
                    new Role(null, "EXTENSION_FIELD_STAFF", slug.toSlug("EXTENSION_FIELD_STAFF"), "/extension/dashboard",new HashSet<>(Arrays.asList(
                            permMap.get("CAN_VIEW_USER"),
                            permMap.get("CAN_FACILITATE_APPLICATIONS"),
                            permMap.get("CAN_RECEIVE_CLAIMS_ON_SITE"),
                            permMap.get("CAN_INSPECT_FIELD"),
                            permMap.get("CAN_ENCODE_CLAIM")
                    )))
            );

            roleRepository.saveAllAndFlush(roles);
            log.info("Saved {} roles for tenant", roles.size());

            log.info("RBAC permissions and roles initialized successfully for pcic");
        } catch (Exception e) {
            log.error("Failed to initialize RBAC for tenant '{}':", e.getMessage(), e);
            throw new RuntimeException("RBAC initialization failed for tenant '"  + "'", e);
        }
    }

    private boolean isRoleNotNull() {
        long permissionCount = permissionRepository.count();
        long roleCount = roleRepository.count();
        log.info("Permission count: {}, Role count: {}", permissionCount, roleCount);
        return permissionCount > 0 || roleCount > 0;
    }
}