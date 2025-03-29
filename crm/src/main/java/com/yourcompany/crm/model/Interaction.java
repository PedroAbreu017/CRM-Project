package com.yourcompany.crm.model;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "interactions")
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com o cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Tipo de interação (por exemplo: EMAIL, CALL, MEETING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;

    // Direção da interação (se foi iniciada pelo cliente ou pela empresa)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionDirection direction;

    // Detalhes da interação
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Campos para rastreamento de tempo
    @Column(name = "interaction_date", nullable = false)
    private LocalDateTime interactionDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Campos para acompanhamento
    @Column(name = "requires_followup")
    private Boolean requiresFollowup;

    @Column(name = "followup_date")
    private LocalDateTime followupDate;

    // Resultado/outcome da interação
    @Enumerated(EnumType.STRING)
    private InteractionOutcome outcome;

    // Campos de auditoria
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (interactionDate == null) {
            interactionDate = LocalDateTime.now();
        }
    }

}