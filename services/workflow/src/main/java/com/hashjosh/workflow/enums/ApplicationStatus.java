package com.hashjosh.workflow.enums;

/**
 * Represents the general status of an application in the workflow.
 * This enum tracks the high-level state of an application as it moves through the system.
 * Specific verification and approval details are managed by their respective services.
 */
public enum ApplicationStatus {
    /** Application has been submitted but not yet processed */
    PENDING,
    
    /** Application is currently under review */
    UNDER_REVIEW,
    
    /** Application has been approved */
    APPROVED,
    
    /** Application has been verified */
    VERIFIED,
    
    /** Policy has been issued for the application */
    POLICY_ISSUED,
    
    /** Claim has been approved for the application */
    CLAIM_APPROVED,
    
    /** Application has been rejected */
    REJECTED,
    
    /** Application has been cancelled */
    CANCELLED
}
