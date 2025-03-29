package com.yourcompany.crm.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@Data 
@Builder
public class AdvancedReportDTO {
    // Métricas gerais
    private BigDecimal totalRevenue;
    private BigDecimal expectedRevenue;
    private Integer totalCustomers;
    private Integer totalOpportunities;
    private BigDecimal averageRevenue;
    private BigDecimal averageTicket;
    private BigDecimal conversionRate;
    
    // Análise temporal
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, Integer> customersByMonth;
    private Map<String, Integer> opportunitiesByMonth;
    private Map<String, BigDecimal> winRateByMonth;
    
    // Análise de pipeline e conversão
    private Map<String, BigDecimal> opportunitiesByStage;
    private Map<String, BigDecimal> conversionByStage;
    private Map<String, Integer> opportunitiesByStatus;
    private BigDecimal averageSalesCycle;
    
    // Análise de segmentação
    private Map<String, BigDecimal> revenueByIndustry;
    private Map<String, BigDecimal> revenueByCustomerType;
    private Map<String, BigDecimal> salesByRegion;
    
    // Performance
    private List<TopCustomerDTO> topCustomers;
    private List<TopSalesDTO> topSalespeople;
    private Map<String, BigDecimal> salesVsTarget;
}