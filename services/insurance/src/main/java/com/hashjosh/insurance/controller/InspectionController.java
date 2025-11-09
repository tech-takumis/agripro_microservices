package com.hashjosh.insurance.controller;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.insurance.dto.inspection.InspectionRequestDto;
import com.hashjosh.insurance.dto.inspection.InspectionResponse;
import com.hashjosh.insurance.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inspection")
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping("/{submissionId}/schedule")
    public ScheduleResponseDto scheduleInspection(@PathVariable UUID submissionId,
                                                  @RequestBody ScheduleRequestDto scheduleDto) {
        return inspectionService.scheduleInspection(submissionId, scheduleDto);
    }

    @GetMapping
    public ResponseEntity<List<InspectionResponse>> getAllInspections() {
        List<InspectionResponse> inspections = inspectionService.getAllInspections();
        return ResponseEntity.ok(inspections);
    }

    @PutMapping("/{submissionId}/inspection")
    public void completeInspection(@PathVariable UUID submissionId,
                                   @RequestBody InspectionRequestDto request) {
        inspectionService.completeInspection(submissionId, request);
    }

    @DeleteMapping("/{inspectionId}" )
    public ResponseEntity<Void> deleteInspection(@PathVariable UUID inspectionId) {
        inspectionService.deleteInspection(inspectionId);
        return ResponseEntity.noContent().build();
    }
}
