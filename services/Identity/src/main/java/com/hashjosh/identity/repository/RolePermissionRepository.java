package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.Permission;
import com.hashjosh.identity.entity.Role;
import com.hashjosh.identity.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    boolean existsByRoleAndPermission(Role role, Permission p);
}
