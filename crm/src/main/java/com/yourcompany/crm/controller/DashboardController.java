package com.yourcompany.crm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.dto.SalesDashboardDTO;
import com.yourcompany.crm.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SalesDashboardDTO> getSalesDashboard() {
        return ResponseEntity.ok(dashboardService.getSalesDashboard());
    }
}