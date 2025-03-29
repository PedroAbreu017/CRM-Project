package com.yourcompany.crm.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopSalesDTO {
    private Long userId;
    private String userName;
    private BigDecimal totalRevenue;
    private Integer totalOpportunities;
    private BigDecimal winRate;
    private BigDecimal averageDealSize;
}