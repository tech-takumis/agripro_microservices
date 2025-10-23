package com.hashjosh.identity.mapper;


import com.hashjosh.constant.user.RoleResponse;
import com.hashjosh.identity.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private  final PermissionMapper permissionMapper;

    public RoleResponse toRoleResponseDto(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .defaultRoute(role.getDefaultRoute())
                .permissions(role.getPermissions().stream()
                        .map(permissionMapper::toPermissionResponse)
                        .toList())
                .build();
    }

    public Role toRoleEntity(RoleResponse dto) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
