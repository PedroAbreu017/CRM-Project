package com.yourcompany.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.dto.InteractionDTO;
import com.yourcompany.crm.service.InteractionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<InteractionDTO>> getAllByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(interactionService.getAllByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InteractionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interactionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InteractionDTO> create(@Valid @RequestBody InteractionDTO interactionDTO) {
        return ResponseEntity.ok(interactionService.create(interactionDTO));
    }

    @GetMapping("/followups")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<InteractionDTO>> getPendingFollowups() {
        return ResponseEntity.ok(interactionService.getPendingFollowups());
    }
}