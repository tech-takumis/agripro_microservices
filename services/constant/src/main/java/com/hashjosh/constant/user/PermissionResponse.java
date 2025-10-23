package com.hashjosh.constant.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
}
