package com.yourcompany.crm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.yourcompany.crm.dto.AdvancedReportDTO;
import com.yourcompany.crm.dto.TopCustomerDTO;
import com.yourcompany.crm.dto.TopSalesDTO;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.OpportunityStatus;
import com.yourcompany.crm.model.PipelineStage;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.OpportunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvancedReportService {
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;

    public AdvancedReportDTO generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        return AdvancedReportDTO.builder()
            // Métricas gerais
            .totalRevenue(calculateTotalRevenue(startDate, endDate))
            .expectedRevenue(calculateExpectedRevenue(startDate, endDate))
            .totalCustomers(calculateTotalCustomers(startDate, endDate))
            .totalOpportunities(calculateTotalOpportunities(startDate, endDate))
            .averageRevenue(calculateAverageRevenue(startDate, endDate))
            .averageTicket(calculateAverageTicket(startDate, endDate))
            .conversionRate(calculateConversionRate(startDate, endDate))
            
            // Análise temporal
            .revenueByMonth(calculateRevenueByMonth(startDate, endDate))
            .customersByMonth(calculateCustomersByMonth(startDate, endDate))
            .opportunitiesByMonth(calculateOpportunitiesByMonth(startDate, endDate))
            .winRateByMonth(calculateWinRateByMonth(startDate, endDate))
            
            // Análise de pipeline e conversão
            .opportunitiesByStage(calculateOpportunitiesByStage(startDate, endDate))
            .conversionByStage(calculateConversionByStage(startDate, endDate))
            .opportunitiesByStatus(calculateOpportunitiesByStatus(startDate, endDate))
            .averageSalesCycle(calculateAverageSalesCycle(startDate, endDate))
            
            // Análise de segmentação
            .revenueByIndustry(calculateRevenueByIndustry(startDate, endDate))
            .revenueByCustomerType(calculateRevenueByCustomerType(startDate, endDate))
            .salesByRegion(calculateSalesByRegion(startDate, endDate))
            
            // Performance
            .topCustomers(findTopCustomers(startDate, endDate))
            .topSalespeople(findTopSalespeople(startDate, endDate))
            .salesVsTarget(calculateSalesVsTarget(startDate, endDate))
            .build();
    }

    private BigDecimal calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return opportunityRepository.sumRevenueByDateRange(startDate, endDate);
    }

    private Integer calculateTotalCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        return customerRepository.countCustomersByDateRange(startDate, endDate);
    }

    private BigDecimal calculateAverageRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = calculateTotalRevenue(startDate, endDate);
        Integer customerCount = calculateTotalCustomers(startDate, endDate);
        return customerCount > 0 ? total.divide(BigDecimal.valueOf(customerCount)) : BigDecimal.ZERO;
    }

    private List<TopCustomerDTO> findTopCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        return opportunityRepository.findTopCustomersByRevenue(
            startDate,
            endDate,
            PageRequest.of(0, 10, Sort.by("value").descending())
        );
    }

    private BigDecimal calculateConversionRate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Opportunity> opportunities = opportunityRepository.findByStatus(OpportunityStatus.WON);
        long totalOpportunities = opportunityRepository.count();
        long wonOpportunities = opportunities.size();

        return totalOpportunities > 0 
            ? BigDecimal.valueOf(wonOpportunities)
                .divide(BigDecimal.valueOf(totalOpportunities), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
    }

    private Map<String, BigDecimal> calculateRevenueByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> revenueData = opportunityRepository.getRevenueByMonth(startDate, endDate);
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        
        for (Object[] row : revenueData) {
            String month = (String) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            revenueByMonth.put(month, revenue);
        }
        
        return revenueByMonth;
    }

    private Map<String, Integer> calculateCustomersByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementar após adicionar a query correspondente no CustomerRepository
        return new LinkedHashMap<>();
    }

    private Map<String, BigDecimal> calculateOpportunitiesByStage(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> stageData = opportunityRepository.countOpportunitiesByStage(startDate, endDate);
        Map<String, BigDecimal> opportunitiesByStage = new LinkedHashMap<>();
        
        for (Object[] row : stageData) {
            PipelineStage stage = (PipelineStage) row[0];
            Long count = (Long) row[1];
            opportunitiesByStage.put(stage.name(), BigDecimal.valueOf(count));
        }
        
        return opportunitiesByStage;
    }

    private Map<String, Integer> calculateOpportunitiesByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> monthData = opportunityRepository.countOpportunitiesByMonth(startDate, endDate);
        Map<String, Integer> opportunitiesByMonth = new LinkedHashMap<>();
        
        for (Object[] row : monthData) {
            String month = (String) row[0];
            Long count = (Long) row[1];
            opportunitiesByMonth.put(month, count.intValue());
        }
        
        return opportunitiesByMonth;
    }


    private Map<String, BigDecimal> calculateRevenueByIndustry(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementar após adicionar a query correspondente no OpportunityRepository
        return new LinkedHashMap<>();
    }

    private Map<String, BigDecimal> calculateRevenueByCustomerType(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementar após adicionar a query correspondente no OpportunityRepository
        return new LinkedHashMap<>();
    }

    private Map<String, BigDecimal> calculateSalesByRegion(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementar após adicionar a query correspondente no OpportunityRepository
        return new LinkedHashMap<>();
    }

    private Map<String, BigDecimal> calculateConversionByStage(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> conversionByStage = new LinkedHashMap<>();
        List<Opportunity> opportunities = opportunityRepository.findAll();
        
        Map<PipelineStage, Long> stageCount = opportunities.stream()
            .collect(Collectors.groupingBy(
                Opportunity::getPipelineStage,
                Collectors.counting()
            ));
        
        long total = opportunities.size();
        if (total > 0) {
            stageCount.forEach((stage, count) -> {
                BigDecimal conversionRate = BigDecimal.valueOf(count)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                conversionByStage.put(stage.name(), conversionRate);
            });
        }
        
        return conversionByStage;
    }

    private Map<String, Integer> calculateOpportunitiesByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Integer> statusCount = new LinkedHashMap<>();
        List<Opportunity> opportunities = opportunityRepository.findAll();
        
        Map<OpportunityStatus, Long> countByStatus = opportunities.stream()
            .collect(Collectors.groupingBy(
                Opportunity::getStatus,
                Collectors.counting()
            ));
        
        countByStatus.forEach((status, count) -> 
            statusCount.put(status.name(), count.intValue())
        );
        
        return statusCount;
    }

    private BigDecimal calculateExpectedRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return opportunityRepository.sumExpectedRevenueByDateRange(startDate, endDate);
    }

    private BigDecimal calculateAverageTicket(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalRevenue = calculateTotalRevenue(startDate, endDate);
        Integer totalOpportunities = calculateTotalOpportunities(startDate, endDate);
        return totalOpportunities > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalOpportunities), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    }

    private Integer calculateTotalOpportunities(LocalDateTime startDate, LocalDateTime endDate) {
        return opportunityRepository.countByDateRange(startDate, endDate);
    }

    private Map<String, BigDecimal> calculateWinRateByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> monthlyStats = opportunityRepository.getWinRateByMonth(startDate, endDate);
        Map<String, BigDecimal> winRateByMonth = new LinkedHashMap<>();
        
        for (Object[] row : monthlyStats) {
            String month = (String) row[0];
            Long totalOpps = (Long) row[1];
            Long wonOpps = (Long) row[2];
            
            BigDecimal winRate = totalOpps > 0 
                ? BigDecimal.valueOf(wonOpps)
                    .divide(BigDecimal.valueOf(totalOpps), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
            
            winRateByMonth.put(month, winRate);
        }
        
        return winRateByMonth;
    }

    private Map<String, BigDecimal> calculateRevenueByStage(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> stageStats = opportunityRepository.getRevenueByStage(startDate, endDate);
        return convertToRevenueMap(stageStats);
    }

    private BigDecimal calculateAverageSalesCycle(LocalDateTime startDate, LocalDateTime endDate) {
        return opportunityRepository.calculateAverageSalesCycle(startDate, endDate);
    }

    private List<TopSalesDTO> findTopSalespeople(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> salesData = opportunityRepository.findTopSalespeopleData(
            startDate, endDate, PageRequest.of(0, 10));
        
        return salesData.stream()
            .map(row -> TopSalesDTO.builder()
                .userId((Long) row[0])
                .userName((String) row[1])
                .totalRevenue((BigDecimal) row[2])
                .totalOpportunities(((Long) row[3]).intValue())
                .winRate((BigDecimal) row[4])
                .averageDealSize((BigDecimal) row[5])
                .build())
            .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> calculateSalesVsTarget(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementar quando tivermos uma entidade para metas de vendas
        return new LinkedHashMap<>();
    }

    private Map<String, BigDecimal> convertToRevenueMap(List<Object[]> data) {
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (Object[] row : data) {
            String key = (String) row[0];
            BigDecimal value = (BigDecimal) row[1];
            revenueMap.put(key != null ? key : "Não especificado", value);
        }
        return revenueMap;
    }
}