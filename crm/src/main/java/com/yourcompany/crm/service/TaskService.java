package com.yourcompany.crm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.TaskDTO;
import com.yourcompany.crm.event.TaskEvent;
import com.yourcompany.crm.model.Customer;
import com.yourcompany.crm.model.Opportunity;
import com.yourcompany.crm.model.Task;
import com.yourcompany.crm.model.TaskPriority;
import com.yourcompany.crm.model.TaskStatus;
import com.yourcompany.crm.model.User;
import com.yourcompany.crm.repository.CustomerRepository;
import com.yourcompany.crm.repository.OpportunityRepository;
import com.yourcompany.crm.repository.TaskRepository;
import com.yourcompany.crm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByAssignee(Long userId) {
        return taskRepository.findByAssignedToId(userId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        return toDTO(task);
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByCustomer(Long customerId) {
        return taskRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByOpportunity(Long opportunityId) {
        return taskRepository.findByOpportunityId(opportunityId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = toEntity(taskDTO);
        task = taskRepository.save(task);
        
        // Abordagem antiga: direta via NotificationService
        notificationService.notifyTaskAssignment(task);
        
        // Nova abordagem: via eventos
        eventPublisher.publishEvent(new TaskEvent.Created(this, task));
        
        return toDTO(task);
    }

    @Transactional
    public TaskDTO assignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        task.setAssignedTo(user);
        task = taskRepository.save(task);
        
        // Abordagem antiga
        notificationService.notifyTaskAssignment(task);
        
        // Nova abordagem
        eventPublisher.publishEvent(new TaskEvent.Assigned(this, task));
        
        return toDTO(task);
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        String previousStatus = task.getStatus().name();
        updateTaskFromDTO(task, taskDTO);
        task = taskRepository.save(task);

        // Abordagem antiga
        if (task.getStatus() == TaskStatus.COMPLETED) {
            notificationService.notifyTaskCompletion(task);
        }
        
        // Nova abordagem
        if (!previousStatus.equals(task.getStatus().name())) {
            eventPublisher.publishEvent(new TaskEvent.StatusChanged(this, task, previousStatus));
            
            if (task.getStatus() == TaskStatus.COMPLETED) {
                eventPublisher.publishEvent(new TaskEvent.Completed(this, task));
            }
        }

        return toDTO(task);
    }
    
    @Transactional
    public TaskDTO completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        String previousStatus = task.getStatus().name();
        task.setStatus(TaskStatus.COMPLETED);
        task = taskRepository.save(task);
        
        // Abordagem antiga
        notificationService.notifyTaskCompletion(task);
        
        // Nova abordagem
        eventPublisher.publishEvent(new TaskEvent.Completed(this, task));
        
        return toDTO(task);
    }
    
    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assignedToId(task.getAssignedTo().getId())
            .customerId(task.getCustomer() != null ? task.getCustomer().getId() : null)
            .opportunityId(task.getOpportunity() != null ? task.getOpportunity().getId() : null)
            .priority(task.getPriority().name())
            .status(task.getStatus().name())
            .dueDate(task.getDueDate())
            .completedDate(task.getCompletedDate())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    private Task toEntity(TaskDTO dto) {
        Task task = new Task();
        return updateTaskFromDTO(task, dto);
    }

    private Task updateTaskFromDTO(Task task, TaskDTO dto) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        
        if (dto.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignedTo);
        }
        
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            task.setCustomer(customer);
        }
        
        if (dto.getOpportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findById(dto.getOpportunityId())
                .orElseThrow(() -> new RuntimeException("Opportunity not found"));
            task.setOpportunity(opportunity);
        }
        
        task.setPriority(TaskPriority.valueOf(dto.getPriority()));
        task.setStatus(TaskStatus.valueOf(dto.getStatus()));
        task.setDueDate(dto.getDueDate());
        task.setCompletedDate(dto.getCompletedDate());
        
        return task;
    }
}