package com.yourcompany.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpportunityDTO {
    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", message = "Value must be positive")
    private BigDecimal value;

    private BigDecimal expectedRevenue;  // Adicionado

    @NotNull(message = "Status is required")
    private String status;

    @Min(0) @Max(100)
    private Integer closingProbability;

    @Future(message = "Expected closing date must be in the future")
    private LocalDateTime expectedClosingDate;

    private LocalDateTime actualClosingDate;  // Adicionado

    private String pipelineStage;  // Adicionado

    private String lostReason;  // Adicionado

    // Campos de auditoria opcionais
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}