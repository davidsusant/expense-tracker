package com.expense.pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.expense.models.Transaction;

public class TransactionsPage extends BasePage {

    // BCA Banking Website Elements
    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/app-nav-menu/ul/li[3]/a")
    private WebElement myAccountMenuLink;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/app-nav-menu/ul/li[3]/div/ul/li[2]/a")
    private WebElement creditCardInformationMenuLink;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/section[2]/ng-component/main/div/div/div[1]/app-side-menu/ul[2]/a[2]")
    private WebElement unbilledTransactionsMenuLink;

    @FindBy(id = "trxTable")
    private WebElement transactionTable;

    @FindBy(xpath = "//*[@id=\"trxTable\"]/table/tbody/tr")
    private List<WebElement> transactionRows;

    // CIMB Banking Website Elements
    @FindBy(xpath = "//*[@id=\"main-navigation\"]/ul/li[1]/a/span")
    private WebElement rekeningSaya;

    @FindBy(xpath = "//*[@id=\"root\"]/div/main/div/div[1]/div/div[3]/div[2]")
    private WebElement kartuKredit;

    @FindBy(xpath = "//*[@id=\"radix-:r1f:\"]/div/div/div/div/div[2]/div/div[1]")
    private WebElement mcPlatinumReguler;

    @FindBy(xpath = "//*[@id=\"root\"]/div/main/div/div[3]/div[2]/div/div/div/div[1]/div/div[2]")
    private WebElement cimbTransactionBody;

    @FindBy(css = "//*[@id=\"root\"]/div/main/div/div[3]/div[2]/div/div/div/div[1]/div/div[2]/div[1]")
    private List<WebElement> cimbTransactionRows;

    public TransactionsPage(WebDriver driver) {
        super(driver);
    }

    // BCA Banking Website Transaction Page
    public void navigateToBcaTransactions() {
        click(myAccountMenuLink);
        click(creditCardInformationMenuLink);
        click(unbilledTransactionsMenuLink);
        waitForPageLoad();
        logger.info("Navigated to BCA transactions page");
    }

    public void navigateToCimbTransactions() {
        click(rekeningSaya);
        waitForPageLoad();
        click(kartuKredit);
        waitForElementToBeVisible(mcPlatinumReguler);
        click(mcPlatinumReguler);
        waitForPageLoad();
        logger.info("Navigated to CIMB transactions page");
    }

    /**
     * Extract all transactions from the table
     * @return
     */
    public List<Transaction> extractBcaTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        waitForElementToBeVisible(transactionTable);
        logger.info("Found {} transactions rows", transactionRows.size());

        for (WebElement row : transactionRows) {
            try {
                Transaction transaction = extractBcaTransactionFromRow(row);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            } catch (Exception e) {
                logger.error("Error extracting transaction from row", e);
            }
        }

        logger.info("Successfully extracted {} transactions", transactions.size());
        return transactions;
    }

    /**
     * Extract transaction data from a single row
     * @param row
     * @return
     */
    private Transaction extractBcaTransactionFromRow(WebElement row) {
        try {
            // Get all cells in the row
            List<WebElement> cells = row.findElements(By.tagName("td"));

            if (cells.size() < 3) {
                logger.warn("Row has insufficient columns");
                return null;
            }

            // Extract data from cells
            String dateStr = cells.get(0).getText().trim();
            String description = cells.get(1).getText().trim();
            String amountStr = cells.get(2).getText().trim();

            // Parse date
            LocalDate date = parseDate(dateStr);

            // Parse amount
            double amount = parseAmount(amountStr);

            // Parse category
            String category = categorizeTransaction(description);

            // Create transaction object
            Transaction transaction = new Transaction();
            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setCategory(category);

            logger.debug("Extracted transaction: {}", transaction);
            return transaction;

        } catch (Exception e) {
            logger.error("Error parsing transaction row", e);
            return null;
        }
    }

    /**
     * Extract all CIMB transactions from Web Element
     * @return
     */
    public List<Transaction> extractCimbTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        waitForElementToBeVisible(cimbTransactionBody);
        logger.info("Found {} transaction rows", cimbTransactionBody.getSize());

        for (WebElement row : cimbTransactionRows) {
            try {
                Transaction transaction = extractCimbTransactionFromRow(row);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            } catch (Exception e) {
                logger.error("Error extracting transaction from row", e);
            }
        }

        logger.info("Successfully extracted {} transactions", transactions.size());
        return transactions;
    }

    /**
     * Extract CIMB transaction data from a single row
     * @param row
     * @return
     */
    private Transaction extractCimbTransactionFromRow(WebElement row) {
        try {
            // Get all cells in the row
            List<WebElement> cells = row.findElements(By.xpath("./div"));

            // Skip rows that don't have the expected 4 columns
            if (cells.size() < 4) {
                logger.warn("Skipping row with {} cells", cells.size());
                return null;
            }

            // Extract data from cells
            String dateStr = cells.get(0).getText().trim();
            String description = cells.get(2).getText().trim();
            String amountStr = cells.get(3).getText().trim();

            // Skip completely empty rows
            if (dateStr.isEmpty()) {
                return null;
            }

            // Parse date
            LocalDate date = parseDate(dateStr);

            // Parse amount
            double amount = parseAmount(amountStr);

            // Create transaction object
            Transaction transaction = new Transaction();
            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setAmount(amount);

            logger.debug("Extracted transaction: {}", transaction);
            return transaction;
        } catch (Exception e) {
            logger.error("Error parsing transaction row", e);
            return null;
        }
    }

    /**
     * Parse date string to LocalDate
     * @param dateStr
     * @return
     */
    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy");

            if (dateStr.equals("PEND")) {
                logger.info("Row with transaction date = 'PEND' has been changed to today date");
                dateStr = LocalDate.now().format(formatter);
            }

            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            logger.error("Error parsing date: {}", dateStr, e);
            return LocalDate.now();
        }
    }

    /**
     * Parse amount string to double
     * @param amountStr
     * @return
     */
    private double parseAmount(String amountStr) {
        try {
            // Remove currency symbols, commas, and spaces
            String cleanAmount = amountStr.replaceAll("[^0-9.-]", "");
            return Double.parseDouble(cleanAmount);
        } catch (NumberFormatException e) {
            logger.error("Error parsing amount: {}", amountStr, e);
            return 0.0;
        }
    }

    /**
     * 
     * @param description
     * @return
     */
    private String categorizeTransaction(String description) {
        String desc = description.toLowerCase();

        if (desc.contains("sbux")) {
            return "Starbucks";
        }
        else if (desc.contains("maison")) {
            return "Bread";
        } 
        else if (desc.contains("hyundai")) {
            return "Car Charging";
        }
        else if (desc.contains("kopitien")) {
            return "Eating";
        }
        else {
            return "Others";
        }
    }
}
