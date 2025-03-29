package com.yourcompany.crm.model;

public enum InteractionOutcome {
  SUCCESSFUL("Successful"),
  FOLLOW_UP_REQUIRED("Follow-up Required"),
  NO_ANSWER("No Answer"),
  POSTPONED("Postponed"),
  CANCELLED("Cancelled"),
  OTHER("Other");

  private final String displayName;

  InteractionOutcome(String displayName) {
      this.displayName = displayName;
  }

  public String getDisplayName() {
      return displayName;
  }
}
