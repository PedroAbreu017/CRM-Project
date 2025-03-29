package com.yourcompany.crm.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class SalesPipelineDTO {
    private String stage;
    private Integer count;
    private BigDecimal value;
    private Double conversionRate;
}