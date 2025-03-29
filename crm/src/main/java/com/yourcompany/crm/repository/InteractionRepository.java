package com.yourcompany.crm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.crm.model.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByCustomerId(Long customerId);
    
    @Query("SELECT i FROM Interaction i WHERE i.requiresFollowup = true AND i.followupDate <= :date")
    List<Interaction> findPendingFollowups(@Param("date") LocalDateTime date);
    
    List<Interaction> findByInteractionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT i FROM Interaction i WHERE CAST(i.followupDate AS LocalDate) = :date")
    List<Interaction> findByFollowupDate(@Param("date") LocalDate date);
}