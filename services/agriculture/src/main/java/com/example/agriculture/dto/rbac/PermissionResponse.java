package com.example.agriculture.dto.rbac;

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
