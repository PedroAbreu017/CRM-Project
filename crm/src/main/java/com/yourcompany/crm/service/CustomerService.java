package com.yourcompany.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.CustomerDTO;
import com.yourcompany.crm.dto.FilterCriteriaDTO; //
import com.yourcompany.crm.exception.ResourceNotFoundException;
import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.CustomerStatus;
import com.yourcompany.crm.model.CustomerType;
import com.yourcompany.crm.model.Region;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;  // Adicione se necessário

    @Transactional(readOnly = true)
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        return customerRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public CustomerDTO create(CustomerDTO dto) {
        Customer customer = toEntity(dto);
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }

    public Page<CustomerDTO> findAll(Specification<Customer> spec, Pageable pageable) {
        return customerRepository.findAll(spec, pageable)
            .map(this::toDTO);
    }
    


    @Transactional(readOnly = true)
    public Page<CustomerDTO> filterCustomers(FilterCriteriaDTO criteria, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            // Filtro por termo de pesquisa
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                String searchPattern = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate namePredicate = cb.like(
                    cb.lower(root.get("name")), searchPattern);
                jakarta.persistence.criteria.Predicate emailPredicate = cb.like(
                    cb.lower(root.get("email")), searchPattern);
                jakarta.persistence.criteria.Predicate companyPredicate = cb.like(
                    cb.lower(root.get("company")), searchPattern);
                
                predicates.add(cb.or(namePredicate, emailPredicate, companyPredicate));
            }
            
            // Filtro por status
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
            
            // Filtro por tipo
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
            
            // Filtro por região
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
            
            // Filtro por data
            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("createdAt"), criteria.getStartDate()));
            }
            
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("createdAt"), criteria.getEndDate()));
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        
        return customerRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional
    public CustomerDTO update(CustomerDTO dto) {
        Customer customer = customerRepository.findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getId()));
        
        updateCustomerFromDTO(customer, dto);
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        customerRepository.delete(customer);
    }


    private CustomerDTO toDTO(Customer customer) {
    CustomerDTO dto = new CustomerDTO();
    dto.setId(customer.getId());
    dto.setName(customer.getName());
    dto.setEmail(customer.getEmail());
    dto.setPhone(customer.getPhone());
    dto.setCompany(customer.getCompany());
    dto.setPosition(customer.getPosition());
    dto.setStatus(customer.getStatus() != null ? customer.getStatus().name() : null);
    dto.setType(customer.getType() != null ? customer.getType().name() : null);
    dto.setIndustrySector(customer.getIndustrySector());
    dto.setAnnualRevenue(customer.getAnnualRevenue());
    dto.setTotalPurchases(customer.getTotalPurchases());
    dto.setLastContact(customer.getLastContact());
    dto.setNextFollowup(customer.getNextFollowup());
    dto.setNotes(customer.getNotes());
    dto.setAssignedUserId(customer.getAssignedUser() != null ? customer.getAssignedUser().getId() : null);
    dto.setCreatedAt(customer.getCreatedAt());
    dto.setUpdatedAt(customer.getUpdatedAt());
    dto.setCreatedBy(customer.getCreatedBy());
    dto.setUpdatedBy(customer.getUpdatedBy());
    return dto;
}

    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setCompany(dto.getCompany());
        customer.setPosition(dto.getPosition());
    
        if (dto.getStatus() != null) {
            try {
                customer.setStatus(CustomerStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
            }
        }
    
        if (dto.getType() != null) {
            try {
                customer.setType(CustomerType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid type: " + dto.getType());
            }
        }
    
        customer.setIndustrySector(dto.getIndustrySector());
        customer.setAnnualRevenue(dto.getAnnualRevenue());
        customer.setTotalPurchases(dto.getTotalPurchases());
        customer.setLastContact(dto.getLastContact());
        customer.setNextFollowup(dto.getNextFollowup());
        customer.setNotes(dto.getNotes());
    
    // O User deve ser carregado do repositório se necessário
    // customer.setAssignedUser(userRepository.findById(dto.getAssignedUserId()).orElse(null));
    
        return customer;
    }

    private void updateCustomerFromDTO(Customer customer, CustomerDTO dto) {
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setCompany(dto.getCompany());
        customer.setPosition(dto.getPosition());
    
        if (dto.getStatus() != null) {
            try {
                customer.setStatus(CustomerStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
            }
        }
    
        if (dto.getType() != null) {
            try {
                customer.setType(CustomerType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid type: " + dto.getType());
            }
        }
    
        customer.setIndustrySector(dto.getIndustrySector());
        customer.setAnnualRevenue(dto.getAnnualRevenue());
        customer.setTotalPurchases(dto.getTotalPurchases());
        customer.setLastContact(dto.getLastContact());
        customer.setNextFollowup(dto.getNextFollowup());
        customer.setNotes(dto.getNotes());
    
        // Se houver um assignedUserId, atualiza o usuário designado
        if (dto.getAssignedUserId() != null) {
            customer.setAssignedUser(userRepository.findById(dto.getAssignedUserId()).orElse(null));
        }
    }

}