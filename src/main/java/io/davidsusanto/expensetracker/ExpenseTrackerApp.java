package io.davidsusanto.expensetracker;

/**
 * Command-line entry point
 * <pre>
 *  java -jar expense-tracker.jar pdf       # parse PDFs in configured directory
 *  java -jar expense-tracker.jar scrape    # scrape bank site with Selenium
 * </pre>
 */
public final class ExpenseTrackerApp {
    
    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }
    }

    // --- Internal methods ---

    private static void printUsageAndExit() {
        System.err.println("Usage: expense-tracker <pdf|scrape>");
        System.exit(1);
    }
}
