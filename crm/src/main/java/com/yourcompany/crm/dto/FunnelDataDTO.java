package com.yourcompany.crm.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunnelDataDTO {
    private List<StageDataDTO> stageData;
    private List<ConversionRateDTO> conversionRates;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageDataDTO {
        private String stage;
        private int count;
        private double value;
        private double averageValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversionRateDTO {
        private String fromStage;
        private String toStage;
        private int fromCount;
        private int toCount;
        private double rate;
    }
}