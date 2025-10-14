package com.example.agriculture.service;


import com.example.agriculture.dto.rbac.PermissionRequest;
import com.example.agriculture.dto.rbac.PermissionResponse;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.mapper.PermissionMapper;
import com.example.agriculture.mapper.RoleMapper;
import com.example.agriculture.repository.PermissionRepository;
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
    private final PermissionMapper permissionMapper;

    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermissionEntity(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public PermissionResponse getPermission(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionResponse updatePermission(UUID permissionId, PermissionRequest request) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @Transactional
    public void deletePermission(UUID permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
