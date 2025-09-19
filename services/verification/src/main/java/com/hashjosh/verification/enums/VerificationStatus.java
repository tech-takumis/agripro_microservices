package com.hashjosh.verification.enums;

public enum VerificationStatus {
    // Municipal Agriculturist statuses
    APPROVED_BY_MA,
    PROCESSING_BY_MA,
    VERIFIED_BY_MA,
    REJECTED_BY_MA,
    CANCELLED_BY_MA,

    // Agricultural Extension Worker statuses
    APPROVED_BY_AEW,
    PROCESSING_BY_AEW,
    VERIFIED_BY_AEW,
    REJECTED_BY_AEW,
    CANCELLED_BY_AEW,

    // General statuses
    APPROVED,
    VERIFIED,
    PENDING,
    CANCELLED,
    REJECTED,
    CANCELLED_BY_USER;
}
