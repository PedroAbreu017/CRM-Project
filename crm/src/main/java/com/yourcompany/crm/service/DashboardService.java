package com.yourcompany.crm.service;

import com.yourcompany.crm.dto.*;
import com.yourcompany.crm.model.*;
import com.yourcompany.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final InteractionRepository interactionRepository;

    @Transactional(readOnly = true)
    public SalesDashboardDTO getSalesDashboard() {
        return SalesDashboardDTO.builder()
            .totalRevenue(calculateTotalRevenue())
            .totalPipeline(calculatePipelineValue())
            .totalOpportunities(countActiveOpportunities())
            .activeCustomers(countActiveCustomers())
            .opportunitiesByStage(getOpportunitiesByStage())
            .topCustomers(getTopCustomers())
            .revenueByMonth(getRevenueByMonth())
            .build();
    }

    private BigDecimal calculateTotalRevenue() {
        return opportunityRepository.calculateTotalWonValue();
    }

    private BigDecimal calculatePipelineValue() {
        return opportunityRepository.findAll().stream()
            .filter(o -> o.getStatus() != OpportunityStatus.WON 
                    && o.getStatus() != OpportunityStatus.LOST)
            .map(o -> o.getValue().multiply(BigDecimal.valueOf(o.getClosingProbability())
                .divide(BigDecimal.valueOf(100))))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer countActiveOpportunities() {
        return opportunityRepository.findAll().stream()
            .filter(o -> o.getStatus() != OpportunityStatus.WON 
                    && o.getStatus() != OpportunityStatus.LOST)
            .collect(Collectors.toList())
            .size();
    }

    private Integer countActiveCustomers() {
        return customerRepository.findByStatus(CustomerStatus.ACTIVE).size();
    }

    private Map<String, Integer> getOpportunitiesByStage() {
        return opportunityRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                o -> o.getPipelineStage().name(),
                Collectors.summingInt(o -> 1)
            ));
    }

    private List<TopCustomerDTO> getTopCustomers() {
        return customerRepository.findTop10ByRevenue().stream()
            .map(customer -> TopCustomerDTO.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .totalRevenue(calculateCustomerRevenue(customer))
                .totalOpportunities(customer.getOpportunities().size())
                .build())
            .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> getRevenueByMonth() {
        return opportunityRepository.findByStatus(OpportunityStatus.WON).stream()
            .collect(Collectors.groupingBy(
                o -> o.getActualClosingDate().getMonth().toString(),
                Collectors.mapping(
                    Opportunity::getValue,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));
    }

    private BigDecimal calculateCustomerRevenue(Customer customer) {
        return customer.getOpportunities().stream()
            .filter(o -> o.getStatus() == OpportunityStatus.WON)
            .map(Opportunity::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}