package com.yourcompany.crm.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.yourcompany.crm.event.OpportunityEvent;
import com.yourcompany.crm.event.TaskEvent;
import com.yourcompany.crm.model.NotificationType;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.Task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    
    private final NotificationService notificationService;
    
    @Async
    @EventListener
    public void handleOpportunityCreated(OpportunityEvent.Created event) {
        Opportunity opportunity = event.getOpportunity();
        log.info("Handling opportunity created event for: {}", opportunity.getTitle());
        
        if (opportunity.getCustomer().getAssignedUser() != null) {
            notificationService.createNotification(
                opportunity.getCustomer().getAssignedUser(),
                "Nova Oportunidade Criada",
                String.format("Nova oportunidade '%s' criada para o cliente %s", 
                    opportunity.getTitle(), opportunity.getCustomer().getName()),
                NotificationType.OPPORTUNITY_STATUS_CHANGE,
                "OPPORTUNITY",
                opportunity.getId()
            );
        }
    }
    
    @Async
    @EventListener
    public void handleOpportunityStatusChanged(OpportunityEvent.StatusChanged event) {
        Opportunity opportunity = event.getOpportunity();
        String previousStatus = event.getPreviousStatus();
        log.info("Handling opportunity status changed event for: {} from {} to {}", 
            opportunity.getTitle(), previousStatus, opportunity.getStatus());
        
        if (opportunity.getCustomer().getAssignedUser() != null) {
            notificationService.createNotification(
                opportunity.getCustomer().getAssignedUser(),
                "Status da Oportunidade Alterado",
                String.format("Oportunidade '%s' mudou de status: %s → %s", 
                    opportunity.getTitle(), previousStatus, opportunity.getStatus()),
                NotificationType.OPPORTUNITY_STATUS_CHANGE,
                "OPPORTUNITY",
                opportunity.getId()
            );
        }
    }
    
    @Async
    @EventListener
    public void handleOpportunityStageChanged(OpportunityEvent.StageChanged event) {
        Opportunity opportunity = event.getOpportunity();
        String previousStage = event.getPreviousStage();
        log.info("Handling opportunity stage changed event for: {} from {} to {}", 
            opportunity.getTitle(), previousStage, opportunity.getPipelineStage());
        
        if (opportunity.getCustomer().getAssignedUser() != null) {
            notificationService.createNotification(
                opportunity.getCustomer().getAssignedUser(),
                "Estágio da Oportunidade Alterado",
                String.format("Oportunidade '%s' mudou de estágio: %s → %s", 
                    opportunity.getTitle(), previousStage, opportunity.getPipelineStage()),
                NotificationType.PIPELINE_STAGE_CHANGE,
                "OPPORTUNITY",
                opportunity.getId()
            );
        }
    }
    
    @Async
    @EventListener
    public void handleTaskCreated(TaskEvent.Created event) {
        Task task = event.getTask();
        log.info("Handling task created event for: {}", task.getTitle());
        
        // Notificações adicionais se necessário
    }
    
    @Async
    @EventListener
    public void handleTaskAssigned(TaskEvent.Assigned event) {
        Task task = event.getTask();
        log.info("Handling task assigned event for: {}", task.getTitle());
        
        if (task.getAssignedTo() != null) {
            notificationService.createNotification(
                task.getAssignedTo(),
                "Nova Tarefa Atribuída",
                String.format("Você foi designado para a tarefa: %s", task.getTitle()),
                NotificationType.TASK_ASSIGNMENT,
                "TASK",
                task.getId()
            );
        }
    }
    
    @Async
    @EventListener
    public void handleTaskCompleted(TaskEvent.Completed event) {
        Task task = event.getTask();
        log.info("Handling task completed event for: {}", task.getTitle());
        
        if (task.getAssignedTo() != null) {
            notificationService.createNotification(
                task.getAssignedTo(),
                "Tarefa Concluída",
                String.format("A tarefa '%s' foi marcada como concluída", task.getTitle()),
                NotificationType.TASK_COMPLETION,
                "TASK",
                task.getId()
            );
        }
    }
}