package com.hashjosh.program.clients;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String slug;
    private List<PermissionResponse> permissions;
}
