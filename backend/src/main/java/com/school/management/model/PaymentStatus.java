package com.school.management.model;

/**
 * Enum representing the payment status of a fee journal entry.
 */
public enum PaymentStatus {
    /**
     * Fee payment is pending
     */
    PENDING,

    /**
     * Fee payment is partial (some amount paid, balance due)
     */
    PARTIAL,

    /**
     * Fee payment is complete
     */
    PAID,

    /**
     * Fee payment is overdue
     */
    OVERDUE
}
