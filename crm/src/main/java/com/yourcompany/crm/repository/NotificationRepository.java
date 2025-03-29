package com.yourcompany.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.model.Notification;
import com.yourcompany.crm.model.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Buscar notificações por usuário, ordenadas por data de criação
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    
    // Buscar notificações não lidas
    List<Notification> findByUser_IdAndReadFalse(Long userId);
    
    // Buscar por tipo de notificação
    List<Notification> findByUser_IdAndType(Long userId, NotificationType type);
    
    // Buscar notificações relacionadas a uma entidade específica
    List<Notification> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    // Contar notificações não lidas
    long countByUser_IdAndReadFalse(Long userId);
    
    // Buscar notificações por período
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Marcar todas as notificações do usuário como lidas
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") Long userId);
    
    // Deletar notificações antigas
    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}