package com.example.agriculture.service;


import com.example.agriculture.dto.rbac.RoleRequest;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.dto.rbac.RoleUpdateRequest;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.entity.Role;
import com.example.agriculture.mapper.RoleMapper;
import com.example.agriculture.repository.PermissionRepository;
import com.example.agriculture.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        List<Permission> permissions = request.getPermissionIds() != null
                ? permissionRepository.findAllById(request.getPermissionIds())
                : List.of();

        Role role = roleMapper.toRoleEntity(request,permissions);
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse getRole(Role role) {
        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse getRoleById(UUID roleId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return getRole(role);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAllWithPermissions()
                .stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleResponse updateRole(UUID roleId, RoleUpdateRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(request.getName() != null ? request.getName() : role.getName());
        role.setDefaultRoute(request.getDefaultRoute() != null ? request.getDefaultRoute() : role.getDefaultRoute());

        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

            // Clear existing permissions and add new ones (modifies in place)
            role.getPermissions().clear();
            role.getPermissions().addAll(permissions);
        }

        roleRepository.save(role);  // This may not be strictly necessary in @Transactional, as changes will flush on commit, but it's fine to keep.
        return roleMapper.toRoleResponse(role);
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        roleRepository.deleteById(roleId);
    }
}
