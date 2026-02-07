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

    public TransactionsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToTransactions() {
        click(myAccountMenuLink);
        click(creditCardInformationMenuLink);
        click(unbilledTransactionsMenuLink);
        waitForPageLoad();
        logger.info("Navigated to transactions page");
    }

    /**
     * Extract all transactions from the table
     * @return
     */
    public List<Transaction> extractTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        waitForElementToBeVisible(transactionTable);
        logger.info("Found {} transactions rows", transactionRows.size());

        for (WebElement row : transactionRows) {
            try {
                Transaction transaction = extractTransactionFromRow(row);
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
    private Transaction extractTransactionFromRow(WebElement row) {
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

            if (amountStr == "PEND") {
                logger.warn("Row with amout = 'PEND' has skipped");
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
}
