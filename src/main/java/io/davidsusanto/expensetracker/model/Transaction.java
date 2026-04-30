package io.davidsusanto.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Canonical transaction shape that both PDF parsers and the Selenium scraper must produce.
 * Immutable by contruction (record).
 * Safe to share across threads without defensive copies.
 */
public record Transaction(
    LocalDate date,
    String description,
    BigDecimal amount,      // use BigDecimal for money, not double
    String currency,        // e.g., IDR, USD, EUR
    Category category,
    String sourceAccount    // e.g., BCA, CIMB
) {
    public Transaction {
        // Compact-constructor validation so a bad Transaction can never exist
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        if (sourceAccount == null || sourceAccount.isBlank()) {
            throw new IllegalArgumentException("sourceAccount must not be blank");
        }
    }
} 
