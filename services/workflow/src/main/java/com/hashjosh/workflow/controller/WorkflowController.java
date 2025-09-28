package com.hashjosh.workflow.controller;

import com.hashjosh.workflow.dto.WorkflowResponseDto;
import com.hashjosh.workflow.service.WorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;


    // Update workflow status
    @PostMapping("/{application-id}/status")
    public ResponseEntity<WorkflowResponseDto> updateWorkflowStatus(
            @PathVariable("application-id")UUID applicationId,
            @RequestParam String status,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(workflowService.updateWorkflowStatus(
                applicationId,status), HttpStatus.CREATED);
    }

    // This get all application in the workflow
    @GetMapping("/{applicationId}")
    public ResponseEntity<List<WorkflowResponseDto>> getWorkflowStatus(
            @PathVariable UUID applicationId,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(workflowService.findWorkflowByApplicationId(applicationId),HttpStatus.FOUND);
    }


    @GetMapping
    public ResponseEntity<List<WorkflowResponseDto>> getWorkflows(
            HttpServletRequest request
    ){
        return new ResponseEntity<>(workflowService.findAllWorkflow(), HttpStatus.OK);
    }



}
