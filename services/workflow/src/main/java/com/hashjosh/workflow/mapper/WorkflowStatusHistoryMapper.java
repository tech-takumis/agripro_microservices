package com.hashjosh.workflow.mapper;

import com.hashjosh.workflow.dto.ApplicationTypeResponseDto;
import com.hashjosh.workflow.dto.UserResponseDto;
import com.hashjosh.workflow.dto.WorkflowResponseDto;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowStatusHistoryMapper {
    public WorkflowResponseDto toWorkflowResponse(WorkflowStatusHistory savedWorkflow,
                                                  UserResponseDto user,
                                                  ApplicationTypeResponseDto applicationType
                                                  ) {
        return new WorkflowResponseDto(
            savedWorkflow.getId(),
                savedWorkflow.getApplicationId(),
                applicationType.name(),
                savedWorkflow.getStatus().name(),
                savedWorkflow.getComments() != null ? savedWorkflow.getComments() : "No comment available",
                savedWorkflow.getUpdatedBy(),
                user.firstName() + " "+user.lastName(),
                savedWorkflow.getUpdatedAt(),
                savedWorkflow.getVersion()
        );
    }
}
