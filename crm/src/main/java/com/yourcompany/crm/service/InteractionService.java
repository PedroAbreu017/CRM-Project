package com.yourcompany.crm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.InteractionDTO;
import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.Interaction;
import com.yourcompany.crm.model.InteractionDirection;
import com.yourcompany.crm.model.InteractionOutcome;
import com.yourcompany.crm.model.InteractionType;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.InteractionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<InteractionDTO> getAllByCustomerId(Long customerId) {
        return interactionRepository.findByCustomerId(customerId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public InteractionDTO create(InteractionDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
            
        Interaction interaction = toEntity(dto);
        interaction.setCustomer(customer);
        
        interaction = interactionRepository.save(interaction);
        
        // Atualiza a data do último contato do cliente
        customer.setLastContact(interaction.getInteractionDate());
        customerRepository.save(customer);
        
        return toDTO(interaction);
    }

    @Transactional(readOnly = true)
    public InteractionDTO getById(Long id) {
        return interactionRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Interaction not found"));
    }

    @Transactional
    public List<InteractionDTO> getPendingFollowups() {
        return interactionRepository.findPendingFollowups(LocalDateTime.now()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private InteractionDTO toDTO(Interaction interaction) {
        InteractionDTO dto = new InteractionDTO();
        dto.setId(interaction.getId());
        dto.setCustomerId(interaction.getCustomer().getId());
        dto.setType(interaction.getType().name());
        dto.setDirection(interaction.getDirection().name());
        dto.setSubject(interaction.getSubject());
        dto.setDescription(interaction.getDescription());
        dto.setInteractionDate(interaction.getInteractionDate());
        dto.setDurationMinutes(interaction.getDurationMinutes());
        dto.setRequiresFollowup(interaction.getRequiresFollowup());
        dto.setFollowUpDate(interaction.getFollowupDate());
        dto.setOutcome(interaction.getOutcome() != null ? interaction.getOutcome().name() : null);
        return dto;
    }

    private Interaction toEntity(InteractionDTO dto) {
        Interaction interaction = new Interaction();
        interaction.setType(InteractionType.valueOf(dto.getType()));
        interaction.setDirection(InteractionDirection.valueOf(dto.getDirection()));
        interaction.setSubject(dto.getSubject());
        interaction.setDescription(dto.getDescription());
        interaction.setInteractionDate(dto.getInteractionDate());
        interaction.setDurationMinutes(dto.getDurationMinutes());
        interaction.setRequiresFollowup(dto.getRequiresFollowup());
        interaction.setFollowupDate(dto.getFollowUpDate());
        if (dto.getOutcome() != null) {
            interaction.setOutcome(InteractionOutcome.valueOf(dto.getOutcome()));
        }
        return interaction;
    }
}