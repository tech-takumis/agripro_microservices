package com.hashjosh.constant;

/**
 * Represents the centralized statuses of an application in the workflow,
 * categorized by role or service usage for type safety and consistency.
 */
public enum ApplicationStatus {

    // General
    SUBMITTED(EventType.APPLICATION_SUBMITTED),
    APPROVED(EventType.APPLICATION_APPROVED),
    VERIFIED(EventType.APPLICATION_SUBMITTED),
    CANCELLED(EventType.APPLICATION_CANCELLED),

    // Verification Service statuses (map to explicit event types)
    APPROVED_BY_MA(EventType.APPLICATION_APPROVED_BY_MA),
    REJECTED_BY_MA(EventType.APPLICATION_REJECTED_BY_MA),
    UNDER_REVIEW_BY_MA(EventType.APPLICATION_UNDER_REVIEW_BY_MA),
    APPROVED_BY_AEW(EventType.APPLICATION_APPROVED_BY_AEW),
    UNDER_REVIEW_BY_AEW(EventType.APPLICATION_UNDER_REVIEW_BY_AEW),
    REJECTED_BY_AEW(EventType.APPLICATION_REJECTED_BY_AEW),

    // Underwriter
    UNDER_REVIEW_BY_UNDERWRITER(EventType.UNDER_REVIEW_BY_UNDERWRITER),
    APPROVED_BY_UNDERWRITER(EventType.APPLICATION_APPROVED_BY_UNDERWRITER),
    REJECTED_BY_UNDERWRITER(EventType.APPLICATION_REJECTED_BY_UNDERWRITER),

    // Adjuster
    UNDER_REVIEW_BY_ADJUSTER(EventType.UNDER_REVIEW_BY_ADJUSTER),
    APPROVED_BY_ADJUSTER(EventType.APPLICATION_APPROVED_BY_ADJUSTER),
    REJECTED_BY_ADJUSTER(EventType.APPLICATION_REJECTED_BY_ADJUSTER),

    // Insurance Service statuses
    POLICY_ISSUED(EventType.POLICY_ISSUED),
    CLAIM_APPROVED(EventType.CLAIM_APPROVED);

    private final EventType eventType;

    ApplicationStatus(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Return the corresponding EventType, or EventType.UNKNOWN if none.
     */
    public EventType toEventType() {
        return this.eventType;
    }
}