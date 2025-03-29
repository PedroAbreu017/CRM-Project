package com.yourcompany.crm.model;

public enum PipelineStage {
    LEAD_IN("Lead In", 1),
    QUALIFICATION("Qualification", 2),
    NEEDS_ANALYSIS("Needs Analysis", 3),
    PROPOSAL("Proposal", 4),
    NEGOTIATION("Negotiation", 5),
    CLOSING("Closing", 6),
    CLOSED("Closed", 7); 

    private final String displayName;
    private final int order;

    PipelineStage(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }
}