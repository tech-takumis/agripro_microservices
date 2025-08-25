package com.hashjosh.workflowservice.controller;

import com.hashjosh.workflowservice.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

//    TODO -  Implement the workflow controller tomorrow
}
