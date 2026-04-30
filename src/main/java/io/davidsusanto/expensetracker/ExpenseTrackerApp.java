package io.davidsusanto.expensetracker;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.davidsusanto.expensetracker.config.AppConfig;
import io.davidsusanto.expensetracker.writer.GoogleSheetsWriter;
import io.davidsusanto.expensetracker.writer.TransactionWriter;

/**
 * Command-line entry point
 * <pre>
 *  java -jar expense-tracker.jar pdf       # parse PDFs in configured directory
 *  java -jar expense-tracker.jar scrape    # scrape bank site with Selenium
 * </pre>
 */
public final class ExpenseTrackerApp {

    private static final Logger log = LoggerFactory.getLogger(ExpenseTrackerApp.class);

    private static final Path CONFIG_FILE = Path.of("config.properties");
    
    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }

        try {
            AppConfig config = AppConfig.load(CONFIG_FILE);
            TransactionWriter writer = buildWriter(config);

            switch (args[0]) {
                case "pdf": runPdf(config, writer);;
                case "scrape": ;
                default: printUsageAndExit();
            }
        } catch (Exception e) {
            log.error("Fatal: {}", e.getMessage(), e);
            System.exit(2);
        }
    }

    // --- Internal methods ---

    private static void runPdf(AppConfig config, TransactionWriter writer) {
        // TODO wire BillingStatementParserRegistry + TransactionCategorizer once parsers exist

        Path dir = Path.of(config.get("pdf.input.directory"));
        log.info("PDF mode - would scan: {} (not yet implemented)", dir);
    }

    private static TransactionWriter buildWriter(AppConfig config) throws Exception {
        return new GoogleSheetsWriter(
            Path.of(config.get("google.sheets.credentials.path")), 
            config.get("google.sheets.spreadsheet.id"),
            config.getOrDefault("google.sheets.default.sheet.name", "Transactions"));
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: expense-tracker <pdf|scrape>");
        System.exit(1);
    }
}
