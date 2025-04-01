package com.yourcompany.crm.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportDTO {
    private Map<String, Object> summary;
    private List<Map<String, Object>> salesByPeriod;
    private List<Map<String, Object>> salesByStatus;
    private List<Map<String, Object>> salesByType;
    private List<Map<String, Object>> salesByUser;
    private List<Map<String, Object>> salesByCustomer;
    private List<Map<String, Object>> salesByRegion;
    private List<Map<String, Object>> salesHistory;
    private List<Map<String, Object>> recentSales;
    
    // Dados para filtros
    private List<UserDTO> users;
    private List<CustomerDTO> customers;
    private List<String> regions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        private Long id;
        private String name;
    }
}