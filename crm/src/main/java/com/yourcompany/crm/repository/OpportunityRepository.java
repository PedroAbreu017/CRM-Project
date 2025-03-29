package com.yourcompany.crm.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.crm.dto.TopCustomerDTO;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.OpportunityStatus;
import com.yourcompany.crm.model.PipelineStage;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long>, JpaSpecificationExecutor<Opportunity> {
    List<Opportunity> findByCustomerId(Long customerId);
    
    List<Opportunity> findByStatus(OpportunityStatus status);
    
    List<Opportunity> findByPipelineStage(PipelineStage stage);
    
    @Query("SELECT o FROM Opportunity o WHERE o.expectedClosingDate <= :date AND o.status NOT IN ('WON', 'LOST', 'CANCELLED')")
    List<Opportunity> findPendingOpportunities(@Param("date") LocalDateTime date);

    List<Opportunity> findByExpectedClosingDateBetweenAndStatus(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        OpportunityStatus status
    );
    
    // Alternativa usando String
    @Query("SELECT o FROM Opportunity o WHERE o.expectedClosingDate BETWEEN :startDate AND :endDate AND o.status = :status")
    List<Opportunity> findByExpectedClosingDateBetweenAndStatusString(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("status") String status
    );
    
    @Query("SELECT SUM(o.value) FROM Opportunity o WHERE o.status = 'WON'")
    BigDecimal calculateTotalWonValue();

    @Query("SELECT SUM(o.value) FROM Opportunity o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
            "SUM(o.value) as revenue " +
            "FROM Opportunity o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') " +
            "ORDER BY month")
    List<Object[]> getRevenueByMonth(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    @Query("SELECT o.pipelineStage, COUNT(o) " +
            "FROM Opportunity o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.pipelineStage")
    List<Object[]> countOpportunitiesByStage(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
                    
    @Query("SELECT c.industrySector, SUM(o.value) " +
            "FROM Opportunity o " +
            "JOIN o.customer c " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.industrySector")
    List<Object[]> getRevenueByIndustry(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.type, SUM(o.value) " +
            "FROM Opportunity o " +
            "JOIN o.customer c " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.type")
    List<Object[]> getRevenueByCustomerType(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.region, SUM(o.value) " +
            "FROM Opportunity o " +
            "JOIN o.customer c " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.region")
    List<Object[]> getSalesByRegion(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
                                    
    @Query("SELECT NEW com.yourcompany.crm.dto.TopCustomerDTO(c.id, c.name, SUM(o.value), COUNT(o.id)) " +
            "FROM Customer c JOIN c.opportunities o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(o.value) DESC")
    List<TopCustomerDTO> findTopCustomersByRevenue(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT o.status, COUNT(o) " +
            "FROM Opportunity o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.status")
    List<Object[]> countOpportunitiesByStatus(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Opportunity o " +
        "WHERE o.createdAt BETWEEN :startDate AND :endDate")
        Integer countOpportunitiesByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
                                                                                     

        @Query("SELECT SUM(o.expectedRevenue) FROM Opportunity o " +
        "WHERE o.createdAt BETWEEN :startDate AND :endDate")
        BigDecimal sumExpectedRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
                
        @Query("SELECT COUNT(o) FROM Opportunity o " +
        "WHERE o.createdAt BETWEEN :startDate AND :endDate")
        Integer countByDateRange(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
                
        @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +                                "COUNT(o) as total, " +
                "COUNT(CASE WHEN o.status = 'WON' THEN 1 END) as won " +
                "FROM Opportunity o " +
                "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') " +
                "ORDER BY month")
        List<Object[]> getWinRateByMonth(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
                
        @Query("SELECT o.pipelineStage, SUM(o.value) " +
                "FROM Opportunity o " +
                "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY o.pipelineStage")
        List<Object[]> getRevenueByStage(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
                "COUNT(o) as total " +
                "FROM Opportunity o " +
                "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') " +
                "ORDER BY month")
        List<Object[]> countOpportunitiesByMonth(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);     
                                                
        @Query("SELECT o.pipelineStage, " +
                "COUNT(o) as total, " +
                "COUNT(CASE WHEN o.status = 'WON' THEN 1 END) as converted " +
                "FROM Opportunity o " +
                "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY o.pipelineStage")
        List<Object[]> getConversionByStage(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);                                        
                                
        @Query("SELECT AVG(TIMESTAMPDIFF(DAY, o.createdAt, o.actualClosingDate)) " +
                "FROM Opportunity o " +
                "WHERE o.status = 'WON' " +
                "AND o.createdAt BETWEEN :startDate AND :endDate " +
                "AND o.actualClosingDate IS NOT NULL")
                BigDecimal calculateAverageSalesCycle(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT u.id, u.username, SUM(o.value), COUNT(o), " +
               "COUNT(CASE WHEN o.status = 'WON' THEN 1 END) * 100.0 / COUNT(o), " +
                "SUM(o.value) / COUNT(o) " +
                "FROM Opportunity o " +
                "JOIN o.assignedUser u " +
                "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY u.id, u.username " +
                "ORDER BY SUM(o.value) DESC")
        List<Object[]> findTopSalespeopleData(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                Pageable pageable);

        List<Opportunity> findByExpectedClosingDateBetweenAndStatus(LocalDateTime now, LocalDateTime oneWeekAhead,
            String string);                                        
}