package com.yourcompany.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourcompany.crm.dto.TaskDTO;
import com.yourcompany.crm.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j  // Adicione esta anotação para habilitar o logging
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        log.info("Requisição recebida para listar todas as tarefas");
        List<TaskDTO> tasks = taskService.getAllTasks();
        log.info("Retornando {} tarefas", tasks.size());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TaskDTO>> getMyTasks(@RequestParam Long userId) {
        log.info("Requisição recebida para listar tarefas do usuário ID: {}", userId);
        List<TaskDTO> tasks = taskService.getTasksByAssignee(userId);
        log.info("Retornando {} tarefas para o usuário ID: {}", tasks.size(), userId);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Requisição recebida para criar uma nova tarefa: {}", taskDTO);
        try {
            TaskDTO createdTask = taskService.createTask(taskDTO);
            log.info("Tarefa criada com sucesso, ID: {}", createdTask.getId());
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            log.error("Erro ao criar tarefa: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        log.info("Requisição recebida para atualizar tarefa ID: {}", id);
        try {
            TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
            log.info("Tarefa ID: {} atualizada com sucesso", id);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            log.error("Erro ao atualizar tarefa ID: {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}