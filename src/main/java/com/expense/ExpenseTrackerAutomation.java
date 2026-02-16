package com.expense;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.models.Transaction;
import com.expense.pages.LoginPage;
import com.expense.pages.LogoutPage;
import com.expense.pages.TransactionsPage;
import com.expense.utils.ConfigReader;
import com.expense.utils.DriverManager;
import com.expense.utils.GoogleSheetsService;

public class ExpenseTrackerAutomation {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTrackerAutomation.class);

    private WebDriver driver;

    private LoginPage loginPage;
    private TransactionsPage transactionsPage;
    private LogoutPage logoutPage;
    private GoogleSheetsService googleSheetsService;

    public void setup() {
        logger.info("Starting Expense Tracker Automation...");

        try {
            // Initialize WebDriver
            driver = DriverManager.getDriver();

            // Initialize Page Objects
            loginPage = new LoginPage(driver);
            transactionsPage = new TransactionsPage(driver);
            logoutPage = new LogoutPage(driver);

            // Initialize Google Sheets Service
            googleSheetsService = new GoogleSheetsService();

            logger.info("Setup completed successfully");
        } catch (Exception e) {
            logger.error("Error during setup", e);
            throw new RuntimeException("Setup failed", e);
        }
    }

    // BCA Banking Website Automation
    public void loginToBcaBankingWebsite() {
        logger.info("Starting Login to BCA website...");

        try {
            String bcaBankingUrl = ConfigReader.getBankingUrl();
            String bcaBankingUsername = ConfigReader.getBankingUsername();
            String bcaBankingPassword = ConfigReader.getBankingPassword();

            loginPage.navigateToBcaLoginPage(bcaBankingUrl);
            loginPage.bcaLogin(bcaBankingUsername, bcaBankingPassword);

            logger.info("Login successful");
        } catch (Exception e) {
            logger.error("Login failed", e);
            takeScreenshot("login_failure");
            throw e;
        }
    }

    public void extractTransactionsFromBcaBanking() {
        logger.info("Starting extract transaction from BCA banking...");

        try {
            // Navigate to transaction page
            transactionsPage.navigateToBcaTransactions();

            // Extract transactions
            List<Transaction> transactions = transactionsPage.extractBcaTransactions();

            if (transactions.isEmpty()) {
                logger.warn("No transactions found");
                return;
            }

            logger.info("Extracted {} transactions", transactions.size());

            // Write to Google Sheets
            writeBcaTransactionsToGoogleSheets(transactions);

        } catch (Exception e) {
            logger.error("BCA transaction extraction failed", e);
            takeScreenshot("bca_transactions_failure");
            throw e;
        }
    }

    private void writeBcaTransactionsToGoogleSheets(List<Transaction> transactions) {
        logger.info("Writing to Google Sheets...");

        try {
            String sheetName = ConfigReader.getBcaSheetName();

            // Clear sheets first
            googleSheetsService.clearSheet(sheetName);

            // Write header
            googleSheetsService.writeHeader(sheetName);

            // Append transactions
            googleSheetsService.appendTransactions(transactions, sheetName);

            logger.info("Successfully wrote {} transactions to Google Sheets", transactions.size());
        } catch (IOException e) {
            logger.error("Failed to write to Google Sheets", e);
            throw new RuntimeException("Google Sheets write failed", e);
        }
    }

    public void logoutFromBcaBankingWebsite() {
        logger.info("Starting logout from BCA banking website...");

        try {
            // Click logout button
            logoutPage.bcaLogout();
        } catch (Exception e) {
            logger.error("Logout failed", e);
            takeScreenshot("logout_bca_failure");
            throw e;
        }
    }

    // CIMB Banking Website Automation
    public void loginToCimbBankingWebsite() {
        logger.info("Starting login to CIMB website...");

        try {
            String cimbBankingUrl = ConfigReader.getCimbBankingUrl();
            String cimbBankingUsername = ConfigReader.getCimbBankingUsername();
            String cimbBankingPassword = ConfigReader.getCimbBankingPassword();

            loginPage.navigateToCimbLoginPage(cimbBankingUrl);
            loginPage.cimbLogin(cimbBankingUsername, cimbBankingPassword);

            logger.info("Login to CIMB website succesful");
        } catch (Exception e) {
            logger.error("Login failed", e);
            takeScreenshot("login_cimb_failure");
        }
    }

    public void extractTransactionsFromCimbBanking() {
        logger.info("Starting extract transaction from CIMB banking...");

        try {
            // Navigate to transaction page
            transactionsPage.navigateToCimbTransactions();

            // Extract transactions
            List<Transaction> transactions = transactionsPage.extractCimbTransactions();

            if (transactions.isEmpty()) {
                logger.warn("No transactions found");
                return;
            }

            logger.info("Extracted {} transactions", transactions.size());

            // Write to Google Sheets
            writeCimbTransactionsToGoogleSheets(transactions);
            
        } catch (Exception e) {
            logger.error("CIMB transaction extraction failed", e);
            takeScreenshot("cimb_transactions_failure");
            throw e;
        }
    }

    private void writeCimbTransactionsToGoogleSheets(List<Transaction> transactions) {
        logger.info("Writing to Google Sheets...");

        try {
            String sheetName = ConfigReader.getCimbSheetName();

            // Clear sheets first
            googleSheetsService.clearSheet(sheetName);

            // Write header
            googleSheetsService.writeHeader(sheetName);

            // Append transactions
            googleSheetsService.appendTransactions(transactions, sheetName);

            logger.info("Successfully wrote {} transactions to Google Sheets", transactions.size());
        } catch (IOException e) {
            logger.error("Failed to write to Google Sheets", e);
            throw new RuntimeException("Google Sheets write failed", e);
        }
    }

    public void logoutFromCimbBankingWebsite() {
        logger.info("Starting logout from CIMB website...");

        try {
            // Click logout button
            logoutPage.cimbLogout();
        } catch (Exception e) {
            logger.error("Logout failed", e);
            takeScreenshot("logout_cimb_failure");
            throw e;
        }
    }

    public void tearDown() {
        logger.info("Cleanup...");
        
        if (driver != null) {
            DriverManager.quitDriver();
        }
        
        logger.info("Automation completed.");
    }

    private void takeScreenshot(String screenshotName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            String screenshotPath = ConfigReader.getScreenshotPath() + fileName;

            // Create directory if not exists
            Files.createDirectories(Paths.get(ConfigReader.getScreenshotPath()));

            // Take screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get(screenshotPath));

            logger.info("Screenshot saved: {}", screenshotPath);
        } catch (IOException e) {
            logger.error("Failed to take screenshot", e);
        }
    }

    /**
     * Main method to run the automation directly without TestNG
     */
    public static void main(String[] args) {
        ExpenseTrackerAutomation automation = new ExpenseTrackerAutomation();

        try {
            automation.setup();

            // Credit Card BCA
            automation.loginToBcaBankingWebsite();
            automation.extractTransactionsFromBcaBanking();
            automation.logoutFromBcaBankingWebsite();

            // Credit Card CIMB
            // automation.loginToCimbBankingWebsite();
            // automation.extractTransactionsFromCimbBanking();
            // automation.logoutFromCimbBankingWebsite();

        } catch (Exception e) {
            logger.error("Automation failed", e);
        } finally {
            automation.tearDown();
        }
    }
}
