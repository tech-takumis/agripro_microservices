package com.hashjosh.workflow.service;

import com.hashjosh.workflow.clients.ApplicationClient;
import com.hashjosh.workflow.clients.ApplicationTypeClient;
import com.hashjosh.workflow.clients.UserClient;
import com.hashjosh.workflow.dto.ApplicationResponseDto;
import com.hashjosh.workflow.dto.ApplicationTypeResponseDto;
import com.hashjosh.workflow.dto.UserResponseDto;
import com.hashjosh.workflow.dto.WorkflowResponseDto;
import com.hashjosh.workflow.enums.ApplicationStatus;
import com.hashjosh.workflow.exceptions.ApplicationNotFoundException;
import com.hashjosh.workflow.exceptions.UserNotFoundException;
import com.hashjosh.workflow.mapper.WorkflowStatusHistoryMapper;
import com.hashjosh.workflow.model.WorkflowStatusHistory;
import com.hashjosh.workflow.repository.WorkflowStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
    private final UserClient userClient;
    private final ApplicationClient applicationClient;
    private final ApplicationTypeClient applicationTypeClient;

    public WorkflowResponseDto updateWorkflowStatus(
            UUID applicationId,
            String status, HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        log.info("Update workflow status function token:: : " + token);
        String finalToken = stripBearer(token);

        ApplicationResponseDto application = applicationClient.getApplicationById(finalToken,applicationId, request);

        WorkflowStatusHistory history = new WorkflowStatusHistory(
                application.applicationTypeId(),
                ApplicationStatus.valueOf(status.toUpperCase()),
                application.userId(),
                application.version()
        );

        UserResponseDto user = userClient.findUserById(finalToken,history.getUpdatedBy(), request);
        WorkflowStatusHistory savedWorkflow = workflowStatusRepository.save(history);

        ApplicationTypeResponseDto applicationType = applicationTypeClient.findApplicationTypeById(finalToken,application.applicationTypeId(), request);

        return historyMapper.toWorkflowResponse(savedWorkflow, user, applicationType);
    }

    public List<WorkflowResponseDto> findWorkflowByApplicationId(UUID applicationId, HttpServletRequest request) {

        List<WorkflowStatusHistory> savedHistory =  workflowStatusRepository.findByApplicationId(applicationId);

        return getWorkflows(savedHistory, request);
    }

    public List<WorkflowResponseDto> findAllWorkflow(
            HttpServletRequest request
    ) {
        List<WorkflowStatusHistory> savedHistory =  workflowStatusRepository.findAll();

        return getWorkflows(savedHistory,request);
    }

    public List<WorkflowResponseDto> getWorkflows(List<WorkflowStatusHistory> histories, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("Get workflows function token:: : " + token);

        String finalToken = stripBearer(token);

        List<WorkflowResponseDto> workflowResponseDtos = new ArrayList<>();
        for (WorkflowStatusHistory history : histories) {
            try {
                UserResponseDto user = userClient.findUserById(finalToken, history.getUpdatedBy(), request);
                ApplicationResponseDto application = applicationClient.getApplicationById(finalToken, history.getApplicationId(), request);
                ApplicationTypeResponseDto applicationType = applicationTypeClient.findApplicationTypeById(finalToken, application.applicationTypeId(), request);
                workflowResponseDtos.add(historyMapper.toWorkflowResponse(history, user, applicationType));

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


    public static String stripBearer(String header) {
        if (header == null) return null;
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}
