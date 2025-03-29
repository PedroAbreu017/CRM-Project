package com.yourcompany.crm.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Informações básicas do cliente
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    private String company;
    private String position;

    // Informações de classificação e segmentação
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;
    
    @Column(name = "customer_type")
    @Enumerated(EnumType.STRING)
    private CustomerType type;
    
    @Column(name = "industry_sector")
    private String industrySector;
    
    // Informações financeiras
    @Column(name = "annual_revenue")
    private Double annualRevenue;
    
    @Column(name = "total_purchases")
    private Double totalPurchases;

    // Informações de relacionamento
    @Column(name = "last_contact")
    private LocalDateTime lastContact;
    
    @Column(name = "next_followup")
    private LocalDateTime nextFollowup;
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Relacionamentos
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<Opportunity> opportunities = new HashSet<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<Interaction> interactions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;  // Usuário responsável pelo cliente

    // Campos de auditoria
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}