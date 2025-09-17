package com.hashjosh.program.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.program.enums.ProgramStatus;
import com.hashjosh.program.enums.ProgramType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class ProgramUpdateRequestDto {
    private String name;
    private ProgramType type;
    private ProgramStatus status;
    private int completion;
    private JsonNode extraFields;
}
