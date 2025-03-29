package com.yourcompany.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class FilterCriteriaDTO {
    private String searchTerm;
    private List<String> status;
    private List<String> stages;
    private List<String> regions;
    private List<String> types;
    private List<String> industries;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}