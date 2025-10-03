package com.hashjosh.workflow.service;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.workflow.clients.ApplicationClient;
import com.hashjosh.workflow.clients.ApplicationTypeClient;
import com.hashjosh.workflow.config.CustomUserDetails;
import com.hashjosh.workflow.dto.ApplicationResponseDto;
import com.hashjosh.workflow.dto.ApplicationTypeResponseDto;
import com.hashjosh.workflow.dto.WorkflowResponseDto;
import com.hashjosh.workflow.mapper.WorkflowStatusHistoryMapper;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final WorkflowStatusRepository workflowStatusRepository;
    private final WorkflowStatusHistoryMapper historyMapper;
    private final ApplicationClient applicationClient;
    private final ApplicationTypeClient applicationTypeClient;

    public WorkflowResponseDto updateWorkflowStatus(
            UUID applicationId,
            String status) {
        
        // Get authentication from security context
        CustomUserDetails userDetails = getCurrentUser();
        String token = userDetails.getToken();
        
        ApplicationResponseDto application = applicationClient.getApplicationById(
            token, 
            applicationId, 
            null // No need for request object since we have the token
        );

        // Validate and map the status
        ApplicationStatus applicationStatus;
        try {
            applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid status: " + status + ". Valid statuses are: " + 
                String.join(", ", getValidStatuses())
            );
        }

        WorkflowStatusHistory history = new WorkflowStatusHistory(
                application.applicationTypeId(),
                applicationStatus,
                application.userId(),
                application.version()
        );

        // Get user and application type using the token from security context
        WorkflowStatusHistory savedWorkflow = workflowStatusRepository.save(history);

        ApplicationTypeResponseDto applicationType = applicationTypeClient.findApplicationTypeById(
            token,
            application.applicationTypeId(), 
            null // No need for request object
        );

        return historyMapper.toWorkflowResponse(savedWorkflow, userDetails, applicationType);
    }

    public List<WorkflowResponseDto> findWorkflowByApplicationId(UUID applicationId) {
        List<WorkflowStatusHistory> savedHistory = workflowStatusRepository.findByApplicationId(applicationId);
        return getWorkflows(savedHistory);
    }

    public List<WorkflowResponseDto> findAllWorkflow() {
        List<WorkflowStatusHistory> savedHistory = workflowStatusRepository.findAll();
        return getWorkflows(savedHistory);
    }

    public List<WorkflowResponseDto> getWorkflows(List<WorkflowStatusHistory> histories) {
        // Get token from security context
        CustomUserDetails userDetails = getCurrentUser();
        String token = userDetails.getToken();

        List<WorkflowResponseDto> workflowResponseDtos = new ArrayList<>();
        for (WorkflowStatusHistory history : histories) {
            try {
                ApplicationResponseDto application = applicationClient.getApplicationById(
                    token, 
                    history.getApplicationId(), 
                    null
                );
                ApplicationTypeResponseDto applicationType = applicationTypeClient.findApplicationTypeById(
                    token, 
                    application.applicationTypeId(), 
                    null
                );
                workflowResponseDtos.add(historyMapper.toWorkflowResponse(history, userDetails, applicationType));

            } catch (RuntimeException e) {
                log.error("Failed to process workflow history {}: {}", history.getId(), e.getMessage());
                if (e.getMessage().contains("403")) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to user service: " + e.getMessage(), e);
                }
                throw e;
            }
        }
        return workflowResponseDtos;
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, 
                "No authenticated user found in security context"
            );
        }
        
        return (CustomUserDetails) authentication.getPrincipal();
    }
    
    private List<String> getValidStatuses() {
        List<String> statuses = new ArrayList<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            statuses.add(status.name());
        }
        return statuses;
    }
}
