package com.yourcompany.crm.service;

import com.yourcompany.crm.dto.FilterCriteriaDTO;
import com.yourcompany.crm.model.Opportunity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpportunityFilterService extends FilterService<Opportunity> {

    @Override
    public Specification<Opportunity> createSpecification(FilterCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Pesquisa em múltiplos campos
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                String searchTerm = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm));
                searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm));
                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }
            
            // Filtro por status
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates.add(root.get("status").in(criteria.getStatus()));
            }
            
            // Filtro por estágio
            if (criteria.getStages() != null && !criteria.getStages().isEmpty()) {
                predicates.add(root.get("pipelineStage").in(criteria.getStages()));
            }
            
            // Filtro por valor
            if (criteria.getMinValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("value"), criteria.getMinValue()));
            }
            
            if (criteria.getMaxValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("value"), criteria.getMaxValue()));
            }
            
            // Filtro por data esperada de fechamento
            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expectedClosingDate"), criteria.getStartDate()));
            }
            
            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expectedClosingDate"), criteria.getEndDate()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}