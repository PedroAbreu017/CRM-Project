package com.yourcompany.crm.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String company;
    private String position;
    private String status;
    private String type;
    private String industrySector;
    private Double annualRevenue;
    private Double totalPurchases;
    private LocalDateTime lastContact;
    private LocalDateTime nextFollowup;
    private String notes;
    private Long assignedUserId;  // Em vez do objeto User completo
    
    // Campos de auditoria - somente leitura
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}