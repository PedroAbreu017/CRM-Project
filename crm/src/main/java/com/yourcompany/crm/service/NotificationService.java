package com.yourcompany.crm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.NotificationDTO;
import com.yourcompany.crm.model.Interaction;
import com.yourcompany.crm.model.Notification;
import com.yourcompany.crm.model.NotificationType;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.Task;
import com.yourcompany.crm.model.User;
import com.yourcompany.crm.repository.NotificationRepository;
import com.yourcompany.crm.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotificationsPaged(Long userId, Pageable pageable) {
        return notificationRepository.findAll(pageable)
            .map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUser_IdAndReadFalse(userId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUser_IdAndReadFalse(userId);
    }
    
    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        
        return toDTO(notification);
    }
    
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }
    
    @Transactional
    public void createNotification(User user, String title, String message, 
                                 NotificationType type, String entityType, Long entityId) {
        if (user == null) {
            log.warn("Attempted to create notification for null user. Title: {}", title);
            return;
        }
        
        log.info("Creating notification: {} for user {}", title, user.getUsername());
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setEntityType(entityType);
        notification.setEntityId(entityId);
        
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void deleteOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        notificationRepository.deleteByCreatedAtBefore(cutoffDate);
    }
    
    // Métodos específicos de notificação para diferentes entidades
    @Transactional
    public void notifyTaskAssignment(Task task) {
        createNotification(
            task.getAssignedTo(),
            "Nova Tarefa Atribuída",
            String.format("Você foi designado para a tarefa: %s", task.getTitle()),
            NotificationType.TASK_ASSIGNMENT,
            "TASK",
            task.getId()
        );
    }

    @Transactional
    public void notifyTaskCompletion(Task task) {
        createNotification(
            task.getAssignedTo(),
            "Tarefa Concluída",
            String.format("A tarefa '%s' foi marcada como concluída", task.getTitle()),
            NotificationType.TASK_COMPLETION,
            "TASK",
            task.getId()
        );
    }
    
    @Transactional
    public void notifyOpportunityStatusChange(Opportunity opportunity, String oldStatus) {
        createNotification(
            opportunity.getCustomer().getAssignedUser(),
            "Status da Oportunidade Alterado",
            String.format("Oportunidade '%s' mudou de status: %s → %s", 
                opportunity.getTitle(), oldStatus, opportunity.getStatus()),
            NotificationType.OPPORTUNITY_STATUS_CHANGE,
            "OPPORTUNITY",
            opportunity.getId()
        );
    }
    
    @Transactional
    public void notifyFollowUpDue(Interaction interaction) {
        createNotification(
            interaction.getCustomer().getAssignedUser(),
            "Lembrete de Follow-up",
            String.format("Follow-up agendado com %s para hoje", interaction.getCustomer().getName()),
            NotificationType.FOLLOW_UP_REMINDER,
            "INTERACTION",
            interaction.getId()
        );
    }
    
    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setEntityType(notification.getEntityType());
        dto.setEntityId(notification.getEntityId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}