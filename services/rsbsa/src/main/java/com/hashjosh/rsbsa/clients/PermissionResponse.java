package com.hashjosh.rsbsa.clients;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class PermissionResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
}
