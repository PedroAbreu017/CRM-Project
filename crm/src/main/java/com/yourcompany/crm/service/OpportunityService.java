package com.yourcompany.crm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.OpportunityDTO;
import com.yourcompany.crm.event.OpportunityEvent;
import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.OpportunityStatus;
import com.yourcompany.crm.model.PipelineStage;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.OpportunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<OpportunityDTO> getAll() {
        return opportunityRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityDTO getById(Long id) {
        return opportunityRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Opportunity not found"));
    }

    @Transactional(readOnly = true)
    public List<OpportunityDTO> getAllByCustomerId(Long customerId) {
        return opportunityRepository.findByCustomerId(customerId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public OpportunityDTO create(OpportunityDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
            
        Opportunity opportunity = toEntity(dto);
        opportunity.setCustomer(customer);
        
        opportunity = opportunityRepository.save(opportunity);
        
        // Abordagem antiga
        // (Comentado porque não existia no código original)
        
        // Nova abordagem: via eventos
        eventPublisher.publishEvent(new OpportunityEvent.Created(this, opportunity));
        
        return toDTO(opportunity);
    }

    @Transactional
    public OpportunityDTO update(Long id, OpportunityDTO dto) {
        Opportunity opportunity = opportunityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Opportunity not found"));
            
        updateFromDTO(opportunity, dto);
        opportunity = opportunityRepository.save(opportunity);
        return toDTO(opportunity);
    }

    @Transactional
    public void delete(Long id) {
        opportunityRepository.deleteById(id);
    }

    @Transactional
    public OpportunityDTO updateStatus(Long id, String status) {
        Opportunity opportunity = opportunityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Opportunity not found"));
        
        String previousStatus = opportunity.getStatus().name();
        opportunity.setStatus(OpportunityStatus.valueOf(status));
        opportunity = opportunityRepository.save(opportunity);
        
        // Abordagem antiga
        notificationService.notifyOpportunityStatusChange(opportunity, previousStatus);
        
        // Nova abordagem: via eventos
        eventPublisher.publishEvent(new OpportunityEvent.StatusChanged(this, opportunity, previousStatus));
        
        return toDTO(opportunity);
    }

    @Transactional
    public OpportunityDTO updatePipelineStage(Long id, String stage) {
        Opportunity opportunity = opportunityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Opportunity not found"));
        
        String previousStage = opportunity.getPipelineStage().name();
        opportunity.setPipelineStage(PipelineStage.valueOf(stage));
        opportunity = opportunityRepository.save(opportunity);
        
        // Nova abordagem: via eventos
        eventPublisher.publishEvent(new OpportunityEvent.StageChanged(this, opportunity, previousStage));
        
        return toDTO(opportunity);
    }

    @Transactional(readOnly = true)
    public List<OpportunityDTO> getPendingOpportunities() {
        return opportunityRepository.findPendingOpportunities(LocalDateTime.now()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // Métodos de conversão DTO <-> Entity
    private OpportunityDTO toDTO(Opportunity opportunity) {
        OpportunityDTO dto = new OpportunityDTO();
        dto.setId(opportunity.getId());
        dto.setCustomerId(opportunity.getCustomer().getId());
        dto.setTitle(opportunity.getTitle());
        dto.setDescription(opportunity.getDescription());
        dto.setValue(opportunity.getValue());
        dto.setExpectedRevenue(opportunity.getExpectedRevenue());
        dto.setStatus(opportunity.getStatus().name());
        dto.setClosingProbability(opportunity.getClosingProbability());
        dto.setExpectedClosingDate(opportunity.getExpectedClosingDate());
        dto.setActualClosingDate(opportunity.getActualClosingDate());
        dto.setPipelineStage(opportunity.getPipelineStage().name());
        dto.setLostReason(opportunity.getLostReason());
        return dto;
    }

    private Opportunity toEntity(OpportunityDTO dto) {
        Opportunity opportunity = new Opportunity();
        updateFromDTO(opportunity, dto);
        return opportunity;
    }
    
    private void updateFromDTO(Opportunity opportunity, OpportunityDTO dto) {
        opportunity.setTitle(dto.getTitle());
        opportunity.setDescription(dto.getDescription());
        opportunity.setValue(dto.getValue());
        opportunity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunity.setStatus(OpportunityStatus.valueOf(dto.getStatus()));
        opportunity.setClosingProbability(dto.getClosingProbability());
        opportunity.setExpectedClosingDate(dto.getExpectedClosingDate());
        opportunity.setActualClosingDate(dto.getActualClosingDate());
        opportunity.setPipelineStage(PipelineStage.valueOf(dto.getPipelineStage()));
        opportunity.setLostReason(dto.getLostReason());
    }
}    