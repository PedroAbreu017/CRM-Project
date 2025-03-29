package com.yourcompany.crm.event;

import org.springframework.context.ApplicationEvent;

import com.yourcompany.crm.model.Task;

import lombok.Getter;

public abstract class TaskEvent extends ApplicationEvent {
    
    @Getter
    private final Task task;
    
    public TaskEvent(Object source, Task task) {
        super(source);
        this.task = task;
    }
    
    public static class Created extends TaskEvent {
        public Created(Object source, Task task) {
            super(source, task);
        }
    }
    
    public static class Assigned extends TaskEvent {
        public Assigned(Object source, Task task) {
            super(source, task);
        }
    }
    
    public static class StatusChanged extends TaskEvent {
        @Getter
        private final String previousStatus;
        
        public StatusChanged(Object source, Task task, String previousStatus) {
            super(source, task);
            this.previousStatus = previousStatus;
        }
    }
    
    public static class Completed extends TaskEvent {
        public Completed(Object source, Task task) {
            super(source, task);
        }
    }
}