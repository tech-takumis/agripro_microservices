package com.hashjosh.users.dto.permission;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
}
