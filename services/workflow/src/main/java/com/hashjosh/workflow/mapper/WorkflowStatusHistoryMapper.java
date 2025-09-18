package com.hashjosh.workflow.mapper;

import com.hashjosh.workflow.clients.UserResponse;
import com.hashjosh.workflow.dto.ApplicationTypeResponseDto;
import com.hashjosh.workflow.dto.UserResponseDto;
import com.hashjosh.workflow.dto.WorkflowResponseDto;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowStatusHistoryMapper {
    public WorkflowResponseDto toWorkflowResponse(WorkflowStatusHistory savedWorkflow,
                                                  UserResponse user,
                                                  ApplicationTypeResponseDto applicationType
                                                  ) {
        return new WorkflowResponseDto(
            savedWorkflow.getId(),
                savedWorkflow.getApplicationId(),
                applicationType.name(),
                savedWorkflow.getStatus().name(),
                savedWorkflow.getComments() != null ? savedWorkflow.getComments() : "No comment available",
                savedWorkflow.getUpdatedBy(),
                user.getFirstName() + " "+user.getLastName(),
                savedWorkflow.getUpdatedAt(),
                savedWorkflow.getVersion()
        );
    }
}
