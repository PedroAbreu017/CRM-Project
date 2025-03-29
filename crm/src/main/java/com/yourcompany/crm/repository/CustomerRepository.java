package com.yourcompany.crm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.CustomerStatus;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByStatus(CustomerStatus status);

    @Query("SELECT c FROM Customer c LEFT JOIN c.opportunities o WHERE o.status = 'WON' " +
            "GROUP BY c ORDER BY SUM(o.value) DESC")
    List<Customer> findTop10ByRevenue(Pageable pageable);

    default List<Customer> findTop10ByRevenue() {
        return findTop10ByRevenue(PageRequest.of(0, 10));
    }

    // Novas queries para relatórios
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Integer countCustomersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m') as month, " +
            "COUNT(c) as count " +
            "FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m') " +
            "ORDER BY month")
    List<Object[]> getCustomersByMonth(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.type, COUNT(c) " +
            "FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.type")
    List<Object[]> countCustomersByType(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.industrySector, COUNT(c) " +
            "FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.industrySector")
    List<Object[]> countCustomersByIndustry(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
}