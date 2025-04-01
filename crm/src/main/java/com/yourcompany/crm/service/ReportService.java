package com.yourcompany.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourcompany.crm.dto.DashboardDataDTO;
import com.yourcompany.crm.dto.FunnelDataDTO;
import com.yourcompany.crm.dto.SalesReportDTO;
import com.yourcompany.crm.dto.DashboardDataDTO.MetricDTO;
import com.yourcompany.crm.dto.FunnelDataDTO.ConversionRateDTO;
import com.yourcompany.crm.dto.FunnelDataDTO.StageDataDTO;
import com.yourcompany.crm.dto.SalesReportDTO.CustomerDTO;
import com.yourcompany.crm.dto.SalesReportDTO.UserDTO;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.OpportunityRepository;
import com.yourcompany.crm.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Random para gerar dados de exemplo
    private final Random random = new Random();

    /**
     * Obtém dados para o dashboard
     */
    public DashboardDataDTO getDashboardData(Map<String, Object> params) {
        // Implementação temporária com dados de exemplo
        String timeFrame = (String) params.get("timeFrame");
        
        // Métricas de exemplo
        MetricDTO opportunitiesCreated = new MetricDTO(
            120,
            105,
            14.29
        );
        
        MetricDTO totalValue = new MetricDTO(
            2500000.0,
            2200000.0,
            13.64
        );
        
        MetricDTO conversionRate = new MetricDTO(
            27.5,
            24.8,
            10.89
        );
        
        MetricDTO averageTicket = new MetricDTO(
            38500.0,
            35800.0,
            7.54
        );
        
        // Vendas por período
        List<Map<String, Object>> salesByPeriod = new ArrayList<>();
        String[] periods = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};
        for (String period : periods) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("period", period);
            entry.put("value", 200000 + random.nextInt(300000));
            entry.put("target", 350000);
            salesByPeriod.add(entry);
        }
        
        // Oportunidades por status
        List<Map<String, Object>> opportunitiesByStatus = new ArrayList<>();
        String[] statuses = {"OPEN", "QUALIFIED", "PROPOSAL_SENT", "NEGOTIATION", "WON", "LOST"};
        for (String status : statuses) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("status", status);
            entry.put("name", status);
            entry.put("value", 10 + random.nextInt(30));
            opportunitiesByStatus.add(entry);
        }
        
        // Top vendedores
        List<Map<String, Object>> topSalespeople = new ArrayList<>();
        String[] salespeople = {"João Silva", "Maria Santos", "Pedro Almeida", "Ana Oliveira"};
        for (String person : salespeople) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", person);
            entry.put("value", 200000 + random.nextInt(500000));
            topSalespeople.add(entry);
        }
        
        // Top clientes
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        String[] customers = {"Empresa A", "Empresa B", "Empresa C", "Empresa D", "Empresa E"};
        for (String customer : customers) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", customer);
            entry.put("value", 100000 + random.nextInt(400000));
            topCustomers.add(entry);
        }
        
        // Taxas de conversão por estágio
        List<Map<String, Object>> stageConversionRates = new ArrayList<>();
        String[] stages = {"Lead In", "Qualificação", "Análise", "Proposta", "Negociação", "Fechamento"};
        for (String stage : stages) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", stage);
            entry.put("rate", 30 + random.nextInt(50));
            stageConversionRates.add(entry);
        }
        
        return DashboardDataDTO.builder()
            .opportunitiesCreated(opportunitiesCreated)
            .totalValue(totalValue)
            .conversionRate(conversionRate)
            .averageTicket(averageTicket)
            .salesByPeriod(salesByPeriod)
            .opportunitiesByStatus(opportunitiesByStatus)
            .topSalespeople(topSalespeople)
            .topCustomers(topCustomers)
            .stageConversionRates(stageConversionRates)
            .build();
    }

    /**
     * Obtém dados para o relatório de vendas
     */
    public SalesReportDTO getSalesReport(Map<String, Object> params) {
        // Implementação temporária com dados de exemplo
        String timeFrame = (String) params.get("timeFrame");
        
        // Resumo
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSales", 3250000.0);
        summary.put("totalCount", 85);
        summary.put("averageTicket", 38235.29);
        summary.put("largestSale", 250000.0);
        summary.put("closingRate", 27.5);
        summary.put("salesCycleDays", 45);
        summary.put("targetValue", 3500000.0);
        summary.put("targetAchievement", 92.86);
        
        // Vendas por período
        List<Map<String, Object>> salesByPeriod = new ArrayList<>();
        String[] periods = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};
        for (String period : periods) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("period", period);
            entry.put("value", 200000 + random.nextInt(300000));
            entry.put("target", 350000);
            salesByPeriod.add(entry);
        }
        
        // Vendas por status
        List<Map<String, Object>> salesByStatus = new ArrayList<>();
        String[] statuses = {"WON", "LOST", "OPEN", "NEGOTIATION"};
        for (String status : statuses) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("status", status);
            entry.put("value", 200000 + random.nextInt(800000));
            salesByStatus.add(entry);
        }
        
        // Vendas por tipo
        List<Map<String, Object>> salesByType = new ArrayList<>();
        String[] types = {"Novo Cliente", "Cliente Existente", "Renovação", "Expansão"};
        for (String type : types) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("type", type);
            entry.put("value", 150000 + random.nextInt(600000));
            salesByType.add(entry);
        }
        
        // Vendas por usuário
        List<Map<String, Object>> salesByUser = new ArrayList<>();
        String[] salespeople = {"João Silva", "Maria Santos", "Pedro Almeida", "Ana Oliveira"};
        for (int i = 0; i < salespeople.length; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId", i + 1);
            entry.put("userName", salespeople[i]);
            entry.put("value", 200000 + random.nextInt(800000));
            entry.put("count", 10 + random.nextInt(20));
            entry.put("averageValue", 20000 + random.nextInt(20000));
            entry.put("closingRate", 20 + random.nextInt(30));
            entry.put("target", 500000);
            entry.put("targetAchievement", 40 + random.nextInt(60));
            salesByUser.add(entry);
        }
        
        // Vendas por cliente
        List<Map<String, Object>> salesByCustomer = new ArrayList<>();
        String[] customers = {"Empresa A", "Empresa B", "Empresa C", "Empresa D", "Empresa E", 
                             "Empresa F", "Empresa G", "Empresa H", "Empresa I", "Empresa J"};
        for (int i = 0; i < customers.length; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("customerId", i + 1);
            entry.put("customerName", customers[i]);
            entry.put("industry", "Setor " + (i % 4 + 1));
            entry.put("value", 100000 + random.nextInt(200000));
            entry.put("count", 1 + random.nextInt(5));
            entry.put("averageValue", 20000 + random.nextInt(50000));
            
            // Datas de exemplo
            LocalDate firstPurchase = LocalDate.now().minusMonths(random.nextInt(24));
            LocalDate lastPurchase = LocalDate.now().minusMonths(random.nextInt(6));
            
            // Garantir que a última compra não seja antes da primeira
            if (lastPurchase.isBefore(firstPurchase)) {
                lastPurchase = firstPurchase.plusMonths(random.nextInt(3) + 1);
            }
            
            entry.put("firstPurchaseDate", firstPurchase.toString());
            entry.put("lastPurchaseDate", lastPurchase.toString());
            salesByCustomer.add(entry);
        }
        
        // Vendas por região
        List<Map<String, Object>> salesByRegion = new ArrayList<>();
        String[] regions = {"Norte", "Nordeste", "Centro-Oeste", "Sudeste", "Sul"};
        for (String region : regions) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("region", region);
            entry.put("value", 200000 + random.nextInt(1000000));
            entry.put("count", 5 + random.nextInt(20));
            entry.put("averageValue", 30000 + random.nextInt(30000));
            entry.put("percentageOfTotal", 10 + random.nextInt(20));
            entry.put("customerCount", 5 + random.nextInt(15));
            salesByRegion.add(entry);
        }
        
        // Histórico de vendas
        List<Map<String, Object>> salesHistory = new ArrayList<>();
        LocalDate date = LocalDate.now().minusMonths(12);
        for (int i = 0; i < 12; i++) {
            Map<String, Object> entry = new HashMap<>();
            date = date.plusMonths(1);
            entry.put("date", date.format(DateTimeFormatter.ofPattern("MMM/yy")));
            entry.put("value", 200000 + random.nextInt(300000));
            entry.put("target", 300000);
            salesHistory.add(entry);
        }
        
        // Vendas recentes
        List<Map<String, Object>> recentSales = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", 1000 + i);
            entry.put("customerName", customers[i % customers.length]);
            entry.put("userName", salespeople[i % salespeople.length]);
            entry.put("date", now.minusDays(i).toString());
            entry.put("value", 20000 + random.nextInt(100000));
            entry.put("status", statuses[i % statuses.length]);
            entry.put("type", types[i % types.length]);
            recentSales.add(entry);
        }
        
        // Lista de usuários para filtro
        List<UserDTO> users = new ArrayList<>();
        for (int i = 0; i < salespeople.length; i++) {
            users.add(new UserDTO((long) (i + 1), salespeople[i]));
        }
        
        // Lista de clientes para filtro
        List<CustomerDTO> customerList = new ArrayList<>();
        for (int i = 0; i < customers.length; i++) {
            customerList.add(new CustomerDTO((long) (i + 1), customers[i]));
        }
        
        return SalesReportDTO.builder()
            .summary(summary)
            .salesByPeriod(salesByPeriod)
            .salesByStatus(salesByStatus)
            .salesByType(salesByType)
            .salesByUser(salesByUser)
            .salesByCustomer(salesByCustomer)
            .salesByRegion(salesByRegion)
            .salesHistory(salesHistory)
            .recentSales(recentSales)
            .users(users)
            .customers(customerList)
            .regions(List.of(regions))
            .build();
    }

   // Correção para o método getFunnelData no ReportService.java

/**
 * Obtém dados para o relatório de funil
 */
    public FunnelDataDTO getFunnelData(Map<String, Object> params) {
     // Implementação temporária com dados de exemplo
    String timeFrame = (String) params.get("timeFrame");
  
     // Dados do estágio
    List<StageDataDTO> stageData = new ArrayList<>();
    String[] stages = {"LEAD_IN", "QUALIFICATION", "NEEDS_ANALYSIS", "PROPOSAL", "NEGOTIATION", "CLOSING", "CLOSED"};
    int baseCount = 100;
  
    for (String stage : stages) {
        // Usar o padrão builder ao invés do construtor diretamente
        StageDataDTO data = StageDataDTO.builder()
            .stage(stage)
            .count(baseCount)
            .value(baseCount * (20000 + random.nextInt(10000)))
            .averageValue(20000 + random.nextInt(10000))
            .build();
          
        stageData.add(data);
        baseCount = (int) (baseCount * 0.8); // 20% de queda em cada estágio
    }
  
    // Taxas de conversão
    List<ConversionRateDTO> conversionRates = new ArrayList<>();
    for (int i = 0; i < stages.length - 1; i++) {
        int fromCount = stageData.get(i).getCount();
        int toCount = stageData.get(i + 1).getCount();
        double rate = ((double) toCount / fromCount) * 100;
      
        // Usar o padrão builder ao invés do construtor diretamente
        ConversionRateDTO data = ConversionRateDTO.builder()
            .fromStage(stages[i])
            .toStage(stages[i + 1])
            .fromCount(fromCount)
            .toCount(toCount)
            .rate(rate)
            .build();
          
      conversionRates.add(data);
  }
  
  return FunnelDataDTO.builder()
      .stageData(stageData)
      .conversionRates(conversionRates)
      .build();
}

    /**
     * Exporta relatório para CSV
     */
    public byte[] exportReportCsv(String reportType, Map<String, Object> params) {
        String csv = "";
        
        switch (reportType) {
            case "sales":
                // Cabeçalho CSV
                csv = "Período,Valor,Meta\n";
                
                // Dados de exemplo
                String[] periods = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};
                for (String period : periods) {
                    csv += period + "," + (200000 + random.nextInt(300000)) + ",350000\n";
                }
                break;
                
            case "funnel":
                // Cabeçalho CSV
                csv = "Estágio,Quantidade,Valor,Valor Médio\n";
                
                // Dados de exemplo
                String[] stages = {"Lead In", "Qualificação", "Análise", "Proposta", "Negociação", "Fechamento", "Fechado"};
                int count = 100;
                for (String stage : stages) {
                    double value = count * (20000 + random.nextInt(10000));
                    double avgValue = value / count;
                    csv += stage + "," + count + "," + value + "," + avgValue + "\n";
                    count = (int) (count * 0.8);
                }
                break;
        }
        
        return csv.getBytes();
    }

    /**
     * Exporta relatório para Excel
     */
    public byte[] exportReportExcel(String reportType, Map<String, Object> params) {
        // Em uma implementação real, isso geraria um arquivo Excel usando POI
        // Para esta demonstração, simplesmente retornamos um arquivo CSV como se fosse Excel
        return exportReportCsv(reportType, params);
    }

    /**
     * Obtém dados de comparação entre períodos
     */
    public Map<String, Object> getComparisonData(String reportType, String currentPeriod, String previousPeriod) {
        // Implementação temporária com dados de exemplo
        Map<String, Object> result = new HashMap<>();
        
        switch (reportType) {
            case "sales":
                result.put("currentPeriod", Map.of(
                    "name", currentPeriod,
                    "totalSales", 1500000,
                    "averageTicket", 35000,
                    "winRate", 27.5
                ));
                result.put("previousPeriod", Map.of(
                    "name", previousPeriod,
                    "totalSales", 1350000,
                    "averageTicket", 32000,
                    "winRate", 25.0
                ));
                break;
                
            case "funnel":
                List<Map<String, Object>> currentStages = new ArrayList<>();
                List<Map<String, Object>> previousStages = new ArrayList<>();
                
                String[] stages = {"LEAD_IN", "QUALIFICATION", "NEEDS_ANALYSIS", "PROPOSAL", "NEGOTIATION", "CLOSING", "CLOSED"};
                int currentBase = 100;
                int previousBase = 90;
                
                for (String stage : stages) {
                    currentStages.add(Map.of(
                        "stage", stage,
                        "count", currentBase,
                        "value", currentBase * 25000
                    ));
                    previousStages.add(Map.of(
                        "stage", stage,
                        "count", previousBase,
                        "value", previousBase * 25000
                    ));
                    
                    currentBase = (int) (currentBase * 0.8);
                    previousBase = (int) (previousBase * 0.8);
                }
                
                result.put("currentPeriod", Map.of(
                    "name", currentPeriod,
                    "stages", currentStages
                ));
                result.put("previousPeriod", Map.of(
                    "name", previousPeriod,
                    "stages", previousStages
                ));
                break;
        }
        
        return result;
    }
}