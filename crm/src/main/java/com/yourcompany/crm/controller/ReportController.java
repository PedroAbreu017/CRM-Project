package com.yourcompany.crm.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.dto.AdvancedReportDTO;
import com.yourcompany.crm.dto.DashboardDataDTO;
import com.yourcompany.crm.dto.FunnelDataDTO;
import com.yourcompany.crm.dto.SalesReportDTO;
import com.yourcompany.crm.service.AdvancedReportService;
import com.yourcompany.crm.service.ReportService;

@RestController
@RequestMapping("/reports") // Removido o prefixo /api
public class ReportController {
    
    // Usando @Autowired explicitamente em vez de depender do lombok @RequiredArgsConstructor
    @Autowired
    private AdvancedReportService advancedReportService;
    
    @Autowired
    private ReportService reportService;

    @GetMapping("/advanced")
    public ResponseEntity<AdvancedReportDTO> generateAdvancedReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        return ResponseEntity.ok(advancedReportService.generateReport(startDate, endDate));
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDataDTO> getDashboardData(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeFrame", timeFrame != null ? timeFrame : "LAST_30_DAYS");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        DashboardDataDTO data = reportService.getDashboardData(params);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String region) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeFrame", timeFrame != null ? timeFrame : "LAST_30_DAYS");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("userId", userId);
        params.put("customerId", customerId);
        params.put("region", region);
        
        SalesReportDTO data = reportService.getSalesReport(params);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/funnel")
    public ResponseEntity<FunnelDataDTO> getFunnelData(
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeFrame", timeFrame != null ? timeFrame : "LAST_30_DAYS");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        FunnelDataDTO data = reportService.getFunnelData(params);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/comparison")
    public ResponseEntity<Map<String, Object>> getComparisonData(
            @RequestParam String reportType,
            @RequestParam String currentPeriod,
            @RequestParam String previousPeriod) {
        
        Map<String, Object> data = reportService.getComparisonData(reportType, currentPeriod, previousPeriod);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{reportType}/export/csv")
    public ResponseEntity<byte[]> exportReportCsv(
            @PathVariable String reportType,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeFrame", timeFrame != null ? timeFrame : "LAST_30_DAYS");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        byte[] data = reportService.exportReportCsv(reportType, params);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + reportType + "-report.csv")
                .header("Content-Type", "text/csv")
                .body(data);
    }

    @GetMapping("/{reportType}/export/excel")
    public ResponseEntity<byte[]> exportReportExcel(
            @PathVariable String reportType,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("timeFrame", timeFrame != null ? timeFrame : "LAST_30_DAYS");
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        
        byte[] data = reportService.exportReportExcel(reportType, params);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + reportType + "-report.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }
}