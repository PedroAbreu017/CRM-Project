package com.yourcompany.crm.event;

import org.springframework.context.ApplicationEvent;

import com.yourcompany.crm.model.Opportunity;

import lombok.Getter;

public abstract class OpportunityEvent extends ApplicationEvent {
    
    @Getter
    private final Opportunity opportunity;
    
    public OpportunityEvent(Object source, Opportunity opportunity) {
        super(source);
        this.opportunity = opportunity;
    }
    
    public static class Created extends OpportunityEvent {
        public Created(Object source, Opportunity opportunity) {
            super(source, opportunity);
        }
    }
    
    public static class StatusChanged extends OpportunityEvent {
        @Getter
        private final String previousStatus;
        
        public StatusChanged(Object source, Opportunity opportunity, String previousStatus) {
            super(source, opportunity);
            this.previousStatus = previousStatus;
        }
    }
    
    public static class StageChanged extends OpportunityEvent {
        @Getter
        private final String previousStage;
        
        public StageChanged(Object source, Opportunity opportunity, String previousStage) {
            super(source, opportunity);
            this.previousStage = previousStage;
        }
    }
}