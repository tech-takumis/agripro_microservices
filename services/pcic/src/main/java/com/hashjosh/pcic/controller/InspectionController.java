package com.hashjosh.pcic.controller;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.pcic.dto.InspectionRequestDto;
import com.hashjosh.pcic.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pcic/inspection")
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping("/applications/{submissionId}/schedule")
    public ScheduleResponseDto scheduleInspection(@PathVariable UUID submissionId,
                                                  @RequestBody ScheduleRequestDto scheduleDto) {
        return inspectionService.scheduleInspection(submissionId, scheduleDto);
    }

    @PutMapping("/applications/{submissionId}/inspection")
    public void completeInspection(@PathVariable UUID submissionId,
                                   @RequestBody InspectionRequestDto request) {
        inspectionService.completeInspection(submissionId, request.getStatus(), request.getComments());
    }
}
