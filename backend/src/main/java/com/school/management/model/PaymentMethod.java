package com.school.management.model;

/**
 * Enum representing payment methods accepted for fee payments.
 */
public enum PaymentMethod {
    /**
     * Cash payment received at school office
     */
    CASH,

    /**
     * Online payment through payment gateway
     */
    ONLINE,

    /**
     * Cheque payment
     */
    CHEQUE,

    /**
     * Card payment (Debit/Credit)
     */
    CARD
}
