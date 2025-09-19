package com.hashjosh.constant;

/**
 * Represents the centralized statuses of an application in the workflow,
 * categorized by role or service usage for type safety and consistency.
 */
public enum ApplicationStatus {

    // General statuses for all applications
    SUBMITTED,
    APPROVED,
    VERIFIED,
    PENDING,
    CANCELLED,
    REJECTED,
    CANCELLED_BY_USER,

    // Verification Service statuses
    APPROVED_BY_MA,
    REJECTED_BY_MA,
    UNDER_REVIEW_BY_MA,
    APPROVED_BY_AEW,
    UNDER_REVIEW_BY_AEW,
    REJECTED_BY_AEW,

    UNDER_REVIEW_BY_UNDERWRITER,
    APPROVED_BY_UNDERWRITER,
    REJECTED_BY_UNDERWRITER,

    UNDER_REVIEW_BY_ADJUSTER,
    APPROVED_BY_ADJUSTER,
    REJECTED_BY_ADJUSTER,

    // Insurance Service statuses
    POLICY_ISSUED,
    CLAIM_APPROVED;
}