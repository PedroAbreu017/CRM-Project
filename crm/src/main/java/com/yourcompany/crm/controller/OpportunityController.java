package com.yourcompany.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.dto.OpportunityDTO;
import com.yourcompany.crm.service.OpportunityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OpportunityDTO>> getAllByCustomer(
            @RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(opportunityService.getAllByCustomerId(customerId));
        }
        return ResponseEntity.ok(opportunityService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OpportunityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OpportunityDTO> create(@Valid @RequestBody OpportunityDTO opportunityDTO) {
        return ResponseEntity.ok(opportunityService.create(opportunityDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OpportunityDTO> update(
            @PathVariable Long id, 
            @Valid @RequestBody OpportunityDTO opportunityDTO) {
        return ResponseEntity.ok(opportunityService.update(id, opportunityDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OpportunityDTO>> getPendingOpportunities() {
        return ResponseEntity.ok(opportunityService.getPendingOpportunities());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OpportunityDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(opportunityService.updateStatus(id, status));
    }

    @PutMapping("/{id}/stage")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OpportunityDTO> updatePipelineStage(
            @PathVariable Long id,
            @RequestParam String stage) {
        return ResponseEntity.ok(opportunityService.updatePipelineStage(id, stage));
    }
}