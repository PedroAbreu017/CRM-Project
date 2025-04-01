package com.yourcompany.crm.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {
    private MetricDTO opportunitiesCreated;
    private MetricDTO totalValue;
    private MetricDTO conversionRate;
    private MetricDTO averageTicket;
    
    private List<Map<String, Object>> salesByPeriod;
    private List<Map<String, Object>> opportunitiesByStatus;
    private List<Map<String, Object>> topSalespeople;
    private List<Map<String, Object>> topCustomers;
    private List<Map<String, Object>> stageConversionRates;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricDTO {
        private Object current;
        private Object previous;
        private double trend;
    }
}