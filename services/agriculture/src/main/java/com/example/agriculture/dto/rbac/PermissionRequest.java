package com.example.agriculture.dto.rbac;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest {
    private String name;
    private String description;
}
