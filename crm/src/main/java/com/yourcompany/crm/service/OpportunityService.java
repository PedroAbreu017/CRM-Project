package com.yourcompany.crm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.OpportunityDTO;
import com.yourcompany.crm.dto.StageStatDTO;
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

    // Adição ao OpportunityService.java

    public Map<String, List<OpportunityDTO>> getAllGroupedByStage() {
    List<Opportunity> opportunities = opportunityRepository.findAll();
    
    // Agrupar por estágio
    Map<String, List<OpportunityDTO>> result = new HashMap<>();
    
    // Inicializar todos os estágios com listas vazias
    for (PipelineStage stage : PipelineStage.values()) {
        result.put(stage.name(), new ArrayList<>());
    }
    
    // Preencher com oportunidades
    opportunities.forEach(opportunity -> {
        String stage = opportunity.getPipelineStage().name();
        if (!result.containsKey(stage)) {
            result.put(stage, new ArrayList<>());
        }
        result.get(stage).add(toDTO(opportunity));
    });
    
    return result;
}

    public List<StageStatDTO> getStageStatistics() {
    List<Opportunity> opportunities = opportunityRepository.findAll();
    
    Map<PipelineStage, List<Opportunity>> groupedByStage = opportunities.stream()
        .collect(Collectors.groupingBy(Opportunity::getPipelineStage));
    
    List<StageStatDTO> result = new ArrayList<>();
    
    // Calcular estatísticas para cada estágio
    for (PipelineStage stage : PipelineStage.values()) {
        List<Opportunity> stageOpportunities = groupedByStage.getOrDefault(stage, new ArrayList<>());
        
        BigDecimal totalValue = stageOpportunities.stream()
            .map(Opportunity::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgValue = stageOpportunities.isEmpty() 
            ? BigDecimal.ZERO 
            : totalValue.divide(BigDecimal.valueOf(stageOpportunities.size()), 2, RoundingMode.HALF_UP);
        
        result.add(StageStatDTO.builder()
            .stage(stage.name())
            .displayName(stage.getDisplayName())
            .count(stageOpportunities.size())
            .totalValue(totalValue)
            .averageValue(avgValue)
            .build());
    }
    
    // Ordenar por ordem do pipeline
    result.sort(Comparator.comparing(stat -> 
        PipelineStage.valueOf(stat.getStage()).getOrder()));
    
    return result;
}
}    