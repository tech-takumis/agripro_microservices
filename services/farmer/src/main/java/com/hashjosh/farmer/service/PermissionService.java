package com.hashjosh.farmer.service;


import com.hashjosh.farmer.dto.*;
import com.hashjosh.farmer.entity.*;
import com.hashjosh.farmer.mapper.RoleMapper;
import com.hashjosh.farmer.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = roleMapper.toPermission(request);
        permissionRepository.save(permission);
        return roleMapper.toPermissionResponse(permission);
    }

    public PermissionResponse getPermission(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return roleMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(roleMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionResponse updatePermission(UUID permissionId, PermissionRequest request) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        permissionRepository.save(permission);
        return roleMapper.toPermissionResponse(permission);
    }

    @Transactional
    public void deletePermission(UUID permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
