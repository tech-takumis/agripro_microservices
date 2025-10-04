package com.hashjosh.constant.program.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.SchedulePriority;
import com.hashjosh.constant.program.enums.ScheduleType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Data
public class ScheduleResponseDto {
    private UUID id;
    private ScheduleType type;
    private LocalDateTime scheduleDate;
    private SchedulePriority priority;
    private JsonNode metaData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
