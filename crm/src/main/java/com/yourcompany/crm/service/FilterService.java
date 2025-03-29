package com.yourcompany.crm.service;

import com.yourcompany.crm.dto.FilterCriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilterService<T> {

    public Specification<T> createSpecification(FilterCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Adicionar predicados baseados nos critérios
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                List<Predicate> searchPredicates = new ArrayList<>();
                // Adicionar campos de pesquisa específicos da entidade
                // Isso será implementado nas classes filho
                return criteriaBuilder.or(searchPredicates.toArray(new Predicate[0]));
            }
            
            // Implementar outros filtros comuns
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Pageable createPageable(FilterCriteriaDTO criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 10;
        
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(criteria.getSortDirection()) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}