package com.hashjosh.program.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.program.enums.SchedulePriority;
import com.hashjosh.program.enums.ScheduleType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
public class ScheduleRequestDto {
    private ScheduleType type;
    private LocalDateTime scheduleDate;
    private SchedulePriority priority;
    private JsonNode metaData;
}
