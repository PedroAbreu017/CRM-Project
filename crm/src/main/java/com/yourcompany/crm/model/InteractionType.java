package com.yourcompany.crm.model;

public enum InteractionType {
  EMAIL("Email"),
  PHONE_CALL("Phone Call"),
  VIDEO_CALL("Video Call"),
  MEETING("Meeting"),
  SOCIAL_MEDIA("Social Media"),
  SUPPORT_TICKET("Support Ticket"),
  SALES_VISIT("Sales Visit"),
  OTHER("Other");

  private final String displayName;

  InteractionType(String displayName) {
      this.displayName = displayName;
  }

  public String getDisplayName() {
      return displayName;
  }
}