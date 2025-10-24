package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationTypeResponseDto;
import com.hashjosh.application.dto.batch.BatchResponseDTO;
import com.hashjosh.application.model.Batch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchMapper {

    private final ApplicationMapper applicationMapper;
    private final ApplicationTypeMapper applicationTypeMapper;
    public BatchResponseDTO toResponseDTO(Batch batch) {
        List<ApplicationResponseDto> dtos = batch.getApplications().stream()
                .map(applicationMapper::toApplicationResponseDto).toList();

        ApplicationTypeResponseDto typeDto = batch.getApplicationType() != null ?
                applicationTypeMapper.toApplicationResponse(batch.getApplicationType()) : null;

        return  BatchResponseDTO.builder()
                .name(batch.getName())
                .description(batch.getDescription())
                .applications(dtos)
//                .type(typeDto)
                .build();
    }
}
