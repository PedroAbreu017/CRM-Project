package com.yourcompany.crm.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class SalesDashboardDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalPipeline;
    private Integer totalOpportunities;
    private Integer activeCustomers;
    private Map<String, Integer> opportunitiesByStage;
    private List<TopCustomerDTO> topCustomers;
    private Map<String, BigDecimal> revenueByMonth;
}

