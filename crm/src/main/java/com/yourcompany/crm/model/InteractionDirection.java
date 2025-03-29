package com.yourcompany.crm.model;

public enum InteractionDirection {
  INBOUND("Inbound"),    // Cliente iniciou o contato
  OUTBOUND("Outbound");  // Empresa iniciou o contato

  private final String displayName;

  InteractionDirection(String displayName) {
      this.displayName = displayName;
  }

  public String getDisplayName() {
      return displayName;
  }
}
