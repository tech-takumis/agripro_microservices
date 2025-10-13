package com.example.agriculture.dto.rbac;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    private String name;
    private String defaultRoute; // optional
    private List<UUID> permissionIds; // optional
}
