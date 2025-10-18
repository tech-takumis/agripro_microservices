package com.hashjosh.farmer.mapper;

import com.hashjosh.farmer.dto.*;
import com.hashjosh.farmer.entity.*;
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
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .slug(role.getSlug())
                .permissions(role.getPermissions().stream()
                        .map(permissionMapper::toPermissionResponse)
                        .collect(Collectors.toList()))
                .build();
    }


    public Role toEntityRole(RoleRequest request, List<Permission> permissions) {
        return Role.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .permissions(permissions != null ? Set.copyOf(permissions) : Set.of())
                .build();
    }

}
