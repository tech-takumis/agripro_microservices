package com.hashjosh.constant;

/**
 * Centralized definition of all event types in the system for type safety and consistency.
 */
public enum EventType {

    // General application events
    APPLICATION_SUBMITTED,
    APPLICATION_CANCELLED_BY_USER,

    // Verification events
    APPLICATION_APPROVED_BY_MA,
    APPLICATION_REJECTED_BY_MA,
    APPLICATION_APPROVED_BY_AEW,
    APPLICATION_REJECTED_BY_AEW,

    // Underwriter events
    UNDER_REVIEW_BY_UNDERWRITER,
    APPLICATION_APPROVED_BY_UNDERWRITER,
    APPLICATION_REJECTED_BY_UNDERWRITER,

    // Adjuster events
    UNDER_REVIEW_BY_ADJUSTER,
    APPLICATION_APPROVED_BY_ADJUSTER,
    APPLICATION_REJECTED_BY_ADJUSTER,

    // Insurance service events
    POLICY_ISSUED,
    CLAIM_APPROVED;

    /**
     * Utility method to map Kafka event types to EventType.
     * Add mappings here if new event types are introduced.
     */
    public static EventType fromString(String eventType) {
        try {
            return EventType.valueOf(eventType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
}