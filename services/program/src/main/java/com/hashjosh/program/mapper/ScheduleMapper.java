package com.hashjosh.program.mapper;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.program.entity.Schedule;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduleMapper {
    public ScheduleResponseDto toDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .type(schedule.getType())
                .scheduleDate(schedule.getScheduleDate())
                .priority(schedule.getPriority())
                .metaData(schedule.getMetaData())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }

    public Schedule toEntity(ScheduleRequestDto dto) {
        return Schedule.builder()
                .type(dto.getType())
                .scheduleDate(dto.getScheduleDate())
                .priority(dto.getPriority())
                .metaData(dto.getMetaData())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
