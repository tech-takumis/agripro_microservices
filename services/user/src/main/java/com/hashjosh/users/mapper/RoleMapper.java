package com.hashjosh.users.mapper;

import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.users.dto.permission.PermissionRequest;
import com.hashjosh.users.dto.permission.PermissionResponse;
import com.hashjosh.users.dto.role.RoleRequest;
import com.hashjosh.users.dto.role.RoleResponse;
import com.hashjosh.users.entity.Permission;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final SlugUtil slugUtil;

    public RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .slug(role.getSlug())
                .permissions(role.getPermissions().stream()
                        .map(this::toPermissionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .slug(permission.getSlug())
                .description(permission.getDescription())
                .build();
    }

    public Role toRole(RoleRequest request, List<Permission> permissions) {
        return Role.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .permissions(permissions != null ? Set.copyOf(permissions) : Set.of())
                .build();
    }

    public Permission toPermission(PermissionRequest request) {
        return Permission.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .description(request.getDescription())
                .build();
    }
}
