package com.example.agriculture.controller;

import com.example.agriculture.dto.dashboard.MunicipalDashboardResponse;
import com.example.agriculture.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class FrontendDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/municipal-agriculturists")
    public ResponseEntity<MunicipalDashboardResponse> getMunicipalDashboardData() {
        MunicipalDashboardResponse response = dashboardService.getMunicipalDashboardData();
        return ResponseEntity.ok(response);
    }

}
