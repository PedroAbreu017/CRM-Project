package com.yourcompany.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.yourcompany.crm.dto.FilterCriteriaDTO;
import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.CustomerStatus;
import com.yourcompany.crm.model.CustomerType;
import com.yourcompany.crm.model.Region;

import jakarta.persistence.criteria.Predicate;

@Service
public class CustomerFilterService extends FilterService<Customer> {

    @Override
    public Specification<Customer> createSpecification(FilterCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Pesquisa em múltiplos campos
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                String searchTerm = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), searchTerm));
                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }
            
            // Filtro por status - Converte String para Enum
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                List<CustomerStatus> statusEnums = criteria.getStatus().stream()
                    .map(status -> {
                        try {
                            return CustomerStatus.valueOf(status);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid status value: " + status);
                        }
                    })
                    .collect(Collectors.toList());
                
                predicates.add(root.get("status").in(statusEnums));
            }
            
            // Filtro por tipo - Converte String para Enum
            if (criteria.getTypes() != null && !criteria.getTypes().isEmpty()) {
                List<CustomerType> typeEnums = criteria.getTypes().stream()
                    .map(type -> {
                        try {
                            return CustomerType.valueOf(type);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid customer type value: " + type);
                        }
                    })
                    .collect(Collectors.toList());
                
                predicates.add(root.get("type").in(typeEnums));
            }
            
            // Filtro por região - Converte String para Enum
            if (criteria.getRegions() != null && !criteria.getRegions().isEmpty()) {
                List<Region> regionEnums = criteria.getRegions().stream()
                    .map(region -> {
                        try {
                            return Region.valueOf(region);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid region value: " + region);
                        }
                    })
                    .collect(Collectors.toList());
                
                predicates.add(root.get("region").in(regionEnums));
            }
            
            // Filtro por indústria
            if (criteria.getIndustries() != null && !criteria.getIndustries().isEmpty()) {
                predicates.add(root.get("industrySector").in(criteria.getIndustries()));
            }
            
            // Filtro por data de criação
            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getStartDate()));
            }
            
            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.getEndDate()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}