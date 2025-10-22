package com.hashjosh.identity.mapper;

import com.hashjosh.identity.dto.DesignatedRequestDTO;
import com.hashjosh.identity.dto.DesignatedResponseDTO;
import com.hashjosh.identity.entity.Designated;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DesignatedMapper {
    
    public Designated toEntity(DesignatedRequestDTO dto) {
        return Designated.builder()
                .tenantId(dto.getTenantId())
                .userId(dto.getUserId())
                .assignedBy(dto.getAssignedBy())
                .type(dto.getType())
                .assignedAt(dto.getAssignedAt() != null ? dto.getAssignedAt() : LocalDateTime.now())
                .build();
    }

    public DesignatedResponseDTO toResponseDto(Designated entity) {
        return DesignatedResponseDTO.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .assignedBy(entity.getAssignedBy())
                .type(entity.getType())
                .assignedAt(entity.getAssignedAt())
                .build();
    }

    public void updateEntityFromDto(DesignatedRequestDTO dto, Designated entity) {
        entity.setTenantId(dto.getTenantId());
        entity.setUserId(dto.getUserId());
        entity.setAssignedBy(dto.getAssignedBy());
        entity.setType(dto.getType());
        if (dto.getAssignedAt() != null) {
            entity.setAssignedAt(dto.getAssignedAt());
        }
    }
}