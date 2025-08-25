package com.hashjosh.workflowservice.service;

import com.hashjosh.workflowservice.mapper.WorkflowStatusMapper;
import com.hashjosh.workflowservice.repository.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowStatusRepository workflowStatusRepository;
    private final WorkflowStatusMapper workflowStatusMapper;
}
