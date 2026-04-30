package io.davidsusanto.expensetracker.model;

/**
 * Expense categories
 * Extends as needed
 * Keep enum names stable since they'll be written to Google Sheets
 */
public enum Category {
    FOOD,
    GROCERIES,
    TRANSPORT,
    COFFEE,
    SHOPPING,
    UTILITIES,          // electricity, water, internet, phone
    ENTERTAINMENT,
    HEALTH,
    TRAVEL,
    SUBSCRIPTIONS, 
    FEES,
    TRANSFER,
    UNKNOWN
}
