package com.hashjosh.application.controller;

import com.hashjosh.application.service.ApplicationSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/application/sections")
@RequiredArgsConstructor
public class ApplicationSectionController {

    private final ApplicationSectionService applicationSectionService;

}
