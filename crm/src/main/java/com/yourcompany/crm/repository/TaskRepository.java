package com.yourcompany.crm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.crm.model.Task;
import com.yourcompany.crm.model.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long userId);
    
    List<Task> findByCustomerId(Long customerId);
    
    List<Task> findByOpportunityId(Long opportunityId);
    
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime date, TaskStatus status);

    // Alterar de findByDueDateAndStatusName para findByDueDateAndStatus
    List<Task> findByDueDateAndStatus(LocalDate dueDate, TaskStatus status);

     // Alternativa se você precisar usar String
    @Query("SELECT t FROM Task t WHERE t.dueDate = :dueDate AND t.status = :status")
    List<Task> findByDueDateAndStatusString(@Param("dueDate") LocalDate dueDate, @Param("status") String status);
}