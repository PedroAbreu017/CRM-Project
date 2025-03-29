package com.yourcompany.crm.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InteractionDTO {
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Interaction type is required")
    private String type;
    
    @NotNull(message = "Interaction direction is required")
    private String direction;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    private String description;
    
    @NotNull(message = "Interaction date is required")
    private LocalDateTime interactionDate;
    
    private Integer durationMinutes;
    private Boolean requiresFollowup;
    private LocalDateTime FollowUpDate;
    private String outcome;
}