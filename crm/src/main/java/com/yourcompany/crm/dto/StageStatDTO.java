// src/main/java/com/yourcompany/crm/dto/StageStatDTO.java
package com.yourcompany.crm.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageStatDTO {
    private String stage;
    private String displayName;
    private int count;
    private BigDecimal totalValue;
    private BigDecimal averageValue;
}
