package com.yourcompany.crm.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.yourcompany.crm.dto.NotificationDTO;
import com.yourcompany.crm.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RealtimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Envia uma notificação em tempo real para um usuário específico
     */
    public void sendNotificationToUser(User user, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications",
            notification
        );
    }
    
    /**
     * Envia uma notificação sobre uma atualização no contador de notificações não lidas
     */
    public void sendUnreadCountUpdate(User user, long count) {
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications/count",
            count
        );
    }
    
    /**
     * Envia uma notificação para todos os usuários (broadcast)
     */
    public void broadcastNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}