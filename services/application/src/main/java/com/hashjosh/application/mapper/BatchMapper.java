package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.batch.BatchResponseDTO;
import com.hashjosh.application.model.Batch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchMapper {

    public BatchResponseDTO toResponseDTO(Batch batch) {
        return BatchResponseDTO.builder()
                .id(batch.getId())
                .name(batch.getName())
                .description(batch.getDescription())
                .isAvailable(batch.isAvailable())
                .build();
    }
}
