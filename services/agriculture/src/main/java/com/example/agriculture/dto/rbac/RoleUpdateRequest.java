package com.example.agriculture.dto.rbac;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateRequest {
    private String name;
    private String defaultRoute;
    private List<UUID> permissionIds;
}
