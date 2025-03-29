package com.yourcompany.crm.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String entityType;
    private Long entityId;
    private boolean read;
    private LocalDateTime createdAt;
}