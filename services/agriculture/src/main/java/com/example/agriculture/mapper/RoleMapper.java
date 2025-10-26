package com.example.agriculture.mapper;

import com.example.agriculture.dto.rbac.PermissionRequest;
import com.example.agriculture.dto.rbac.PermissionResponse;
import com.example.agriculture.dto.rbac.RoleRequest;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.entity.Role;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final SlugUtil slugUtil;
    private final PermissionMapper permissionMapper;

    public RoleResponse toRoleResponse(Role role) {

        List<PermissionResponse> permissions = permissionMapper
                .toPermissionResponseList(role.getPermissions().stream().toList());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .slug(role.getSlug())
                .permissions(permissions)
                .defaultRoute(role.getDefaultRoute())
                .build();
    }

    public Role toRoleEntity(RoleRequest request, List<Permission> permissions) {
        return Role.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .permissions(permissions != null ? Set.copyOf(permissions) : Set.of())
                .defaultRoute(request.getDefaultRoute())
                .build();
    }

}
