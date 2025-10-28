package com.hashjosh.pcic.dto.role;

import com.hashjosh.pcic.dto.permission.PermissionResponse;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String slug;
    private String defaultRoute;
    private List<PermissionResponse> permissions;
}