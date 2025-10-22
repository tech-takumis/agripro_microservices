package com.hashjosh.identity.dto;

import com.hashjosh.identity.enums.DesignatedType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignatedRequestDTO {
    private UUID tenantId;
    private UUID userId;
    private UUID assignedBy;
    private DesignatedType type;
    private LocalDateTime assignedAt;
}