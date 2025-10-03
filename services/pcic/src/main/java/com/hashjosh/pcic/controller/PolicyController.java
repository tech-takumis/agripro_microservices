package com.hashjosh.pcic.controller;

import com.hashjosh.pcic.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policy")
@Slf4j
public class PolicyController {

    private final PolicyService policyService;
}
