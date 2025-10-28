package com.hashjosh.verification.mapper;

import com.hashjosh.verification.dto.BatchRequestDTO;
import com.hashjosh.verification.dto.BatchResponseDTO;
import com.hashjosh.verification.entity.Batch;
import org.springframework.stereotype.Component;

@Component
public class BatchMapper {
    public Batch toEntity(BatchRequestDTO request) {
        return Batch.builder()
                .applicationTypeId(request.getApplicationTypeId())
                .name(request.getName())
                .description(request.getDescription())
                .isAvailable(request.isAvailable())
                .maxApplications(request.getMaxApplications())
                .endDate(request.getEndDate())
                .startDate(request.getStartDate())
                .build();
    }

    public BatchResponseDTO toBatchResponseDTO(Batch save) {
        return BatchResponseDTO.builder()
                .id(save.getId())
                .applicationTypeId(save.getApplicationTypeId())
                .name(save.getName())
                .description(save.getDescription())
                .maxApplications(save.getMaxApplications())
                .isAvailable(save.isAvailable())
                .endDate(save.getEndDate())
                .startDate(save.getStartDate())
                .build();
    }
}
