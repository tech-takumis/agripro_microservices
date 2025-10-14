package com.example.agriculture.mapper;

import com.example.agriculture.dto.rbac.PermissionRequest;
import com.example.agriculture.dto.rbac.PermissionResponse;
import com.example.agriculture.entity.Permission;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionMapper {

    private final SlugUtil slugUtil;

    public List<PermissionResponse> toPermissionResponseList(List<Permission> permissions) {
        return permissions.stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }

    public Permission toPermissionEntity(PermissionRequest request) {
        return Permission.builder()
                .name(request.getName())
                .slug(slugUtil.toSlug(request.getName()))
                .description(request.getDescription())
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
}
