package com.hashjosh.application.dto.batch;

import com.hashjosh.application.dto.ApplicationResponseDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean isAvailable;
    private List<ApplicationResponseDto> applications;
}
