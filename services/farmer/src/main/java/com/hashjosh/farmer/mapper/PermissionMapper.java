package com.hashjosh.farmer.mapper;

import com.hashjosh.farmer.dto.PermissionRequest;
import com.hashjosh.farmer.dto.PermissionResponse;
import com.hashjosh.farmer.entity.Permission;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionMapper {
    private final SlugUtil slugUtil;

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
