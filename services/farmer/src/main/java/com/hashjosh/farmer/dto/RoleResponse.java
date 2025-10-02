package com.hashjosh.farmer.dto;

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
    private List<PermissionResponse> permissions;
}