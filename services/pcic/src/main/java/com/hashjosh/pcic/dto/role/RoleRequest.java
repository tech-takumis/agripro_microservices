package com.hashjosh.pcic.dto.role;

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
    private List<UUID> permissionIds; // optional
}
