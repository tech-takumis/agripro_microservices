package com.hashjosh.pcic.mapper;

import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.pcic.dto.permission.PermissionRequest;
import com.hashjosh.pcic.dto.permission.PermissionResponse;
import com.hashjosh.pcic.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionMapper {

    private final SlugUtil slugUtil;

    public Permission toPermission(PermissionRequest request) {
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

    public List<PermissionResponse> toPermissionResponseList(List<Permission> permissions) {
        return permissions.stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }
}
