package com.yourcompany.crm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.model.Interaction;
import com.yourcompany.crm.model.NotificationType;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.OpportunityStatus;
import com.yourcompany.crm.model.Task;
import com.yourcompany.crm.model.TaskStatus;
import com.yourcompany.crm.repository.InteractionRepository;
import com.yourcompany.crm.repository.OpportunityRepository;
import com.yourcompany.crm.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledNotificationService {

    private final NotificationService notificationService;
    private final TaskRepository taskRepository;
    private final OpportunityRepository opportunityRepository;
    private final InteractionRepository interactionRepository;
    
    // Executar diariamente às 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void sendDailyTaskReminders() {
        log.info("Sending daily task reminders");
        LocalDate today = LocalDate.now();
        
        // Tarefas com vencimento hoje
        List<Task> dueTasks = taskRepository.findByDueDateAndStatus(today, TaskStatus.IN_PROGRESS);
        
        for (Task task : dueTasks) {
            if (task.getAssignedTo() != null) {
                notificationService.createNotification(
                    task.getAssignedTo(),
                    "Lembrete de Tarefa",
                    String.format("A tarefa '%s' vence hoje", task.getTitle()),
                    NotificationType.TASK_ASSIGNMENT,
                    "TASK",
                    task.getId()
                );
            }
        }
    }
    
    // Executar diariamente às 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void sendFollowUpReminders() {
        log.info("Sending follow-up reminders");
        LocalDate today = LocalDate.now();
    
    // Converta para o início e fim do dia em LocalDateTime
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
    
    // Use o outro método existente que aceita LocalDateTime
        List<Interaction> followUps = interactionRepository.findPendingFollowups(endOfDay);
    // ou crie um novo método
    // List<Interaction> followUps = interactionRepository.findByFollowupDateBetween(startOfDay, endOfDay);
    
        for (Interaction interaction : followUps) {
            if (interaction.getCustomer() != null && interaction.getCustomer().getAssignedUser() != null) {
                notificationService.notifyFollowUpDue(interaction);
                }
            }
    }
    
    // Executar semanalmente nas segundas-feiras às 7:00 AM
    @Scheduled(cron = "0 0 7 * * MON")
    @Transactional
    public void sendWeeklyOpportunityReminders() {
        log.info("Sending weekly opportunity reminders");
        LocalDateTime oneWeekAhead = LocalDateTime.now().plusDays(7);
        
        // Oportunidades com fechamento previsto na próxima semana
        List<Opportunity> closingOpportunities = 
        opportunityRepository.findByExpectedClosingDateBetweenAndStatus(
        LocalDateTime.now(), oneWeekAhead, OpportunityStatus.IN_PROGRESS);

        
        for (Opportunity opportunity : closingOpportunities) {
            if (opportunity.getCustomer() != null && opportunity.getCustomer().getAssignedUser() != null) {
                notificationService.createNotification(
                    opportunity.getCustomer().getAssignedUser(),
                    "Oportunidade a Fechar",
                    String.format("A oportunidade '%s' está prevista para fechar esta semana", 
                        opportunity.getTitle()),
                    NotificationType.OPPORTUNITY_STATUS_CHANGE,
                    "OPPORTUNITY",
                    opportunity.getId()
                );
            }
        }
    }
    
    // Executar todo dia à meia-noite para limpar notificações antigas
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Cleaning up old notifications");
        // Manter notificações por 30 dias
        notificationService.deleteOldNotifications(30);
    }


}