package io.davidsusanto.expensetracker.writer;

import java.util.List;

import io.davidsusanto.expensetracker.model.Transaction;

/**
 * Sink for parsed/scraped transactions.
 * Keeping this as an interface means the core app never imports Google libs directly.
 * Easier to fake in tests and to swap backends (e.g., CSV, SQLite, Notion) later.
 */
public interface TransactionWriter {
    
    void write(List<Transaction> transactions) throws Exception;
}
