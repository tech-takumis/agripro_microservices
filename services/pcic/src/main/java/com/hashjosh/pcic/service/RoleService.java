package com.hashjosh.pcic.service;


import com.hashjosh.pcic.dto.role.RoleRequest;
import com.hashjosh.pcic.dto.role.RoleResponse;
import com.hashjosh.pcic.dto.role.RoleUpdateRequest;
import com.hashjosh.pcic.repository.*;
import com.hashjosh.pcic.entity.*;
import com.hashjosh.pcic.mapper.RoleMapper;

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
    private final PcicRepository pcicRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        List<Permission> permissions = request.getPermissionIds() != null
                ? permissionRepository.findAllById(request.getPermissionIds())
                : List.of();

        Role role = roleMapper.toRole(request,permissions);
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse getRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return roleMapper.toRoleResponse(role);
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

        role.setName(request.getName());

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
