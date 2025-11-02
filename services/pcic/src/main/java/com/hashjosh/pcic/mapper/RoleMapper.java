package com.hashjosh.pcic.mapper;

import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.pcic.dto.permission.PermissionResponse;
import com.hashjosh.pcic.dto.role.RoleRequest;
import com.hashjosh.pcic.dto.role.RoleResponse;
import com.hashjosh.pcic.entity.Permission;
import com.hashjosh.pcic.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final SlugUtil slugUtil;
    private final PermissionMapper permissionMapper;

    public RoleResponse toRoleResponse(Role role) {
        List<PermissionResponse> permissionResponses = permissionMapper
                .toPermissionResponseList(role.getPermissions().stream().toList());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .slug(role.getSlug())
                .defaultRoute(role.getDefaultRoute())
                .permissions(permissionResponses)
                .build();
    }


    public Role toRole(RoleRequest request, List<Permission> permissions) {
        return Role.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .permissions(permissions != null ? Set.copyOf(permissions) : Set.of())
                .build();
    }

}
