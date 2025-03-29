package com.yourcompany.crm.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class TopCustomerDTO {
    private Long customerId;
    private String customerName;
    private BigDecimal totalRevenue;
    private Integer totalOpportunities;
}