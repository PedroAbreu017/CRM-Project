package com.yourcompany.crm.model;

public enum OpportunityStatus {
    OPEN("Open"),
    QUALIFIED("Qualified"),
    PROPOSAL_SENT("Proposal Sent"),
    NEGOTIATION("Negotiation"),
    WON("Won"),           // Adicionado
    LOST("Lost"),         // Adicionado
    IN_PROGRESS("In Progress"),
    CANCELLED("Cancelled");

    private final String displayName;

    OpportunityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}