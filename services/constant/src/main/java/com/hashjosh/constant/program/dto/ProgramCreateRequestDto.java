package com.hashjosh.constant.program.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.ProgramStatus;
import com.hashjosh.constant.program.enums.ProgramType;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgramCreateRequestDto {
    private String name;
    private ProgramType type;
    private ProgramStatus status;
    private int completion;
    private JsonNode extraFields;
}
