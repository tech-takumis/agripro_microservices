package com.hashjosh.constant.program.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.ProgramStatus;
import com.hashjosh.constant.program.enums.ProgramType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class ProgramResponseDto {
    private UUID id;
    private String name;
    private ProgramType type;
    private ProgramStatus status;
    private int completion;
    private JsonNode extraFields;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
