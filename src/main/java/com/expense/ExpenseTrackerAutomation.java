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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    @BeforeClass
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

    @Test(priority = 1)
    public void loginToBankingWebsite() {
        logger.info("Starting Login...");

        try {
            String bankingUrl = ConfigReader.getBankingUrl();
            String bankingUsername = ConfigReader.getBankingUsername();
            String bankingPassword = ConfigReader.getBankingPassword();

            loginPage.navigateToLoginPage(bankingUrl);
            loginPage.login(bankingUsername, bankingPassword);

            logger.info("Login successful");
        } catch (Exception e) {
            logger.error("Login failed", e);
            takeScreenshot("login_failure");
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = "loginToBankingWebsite")
    public void extractTransactionsFromBanking() {
        logger.info("Starting Extract Transaction...");

        try {
            // Navigate to transaction page
            transactionsPage.navigateToTransactions();

            // Extract transactions
            List<Transaction> transactions = transactionsPage.extractTransactions();

            if (transactions.isEmpty()) {
                logger.warn("No transactions found");
                return;
            }

            logger.info("Extracted {} transactions", transactions.size());

            // Log sample transactions
            for (int i = 0; i < Math.min(3, transactions.size()); i++) {
                logger.info("Sample transaction {}: {}", i + 1, transactions.get(i));
            }

            // Write to Google Sheets
            writeTransactionsToGoogleSheets(transactions);

        } catch (Exception e) {
            logger.error("Transaction extraction failed", e);
            takeScreenshot("transactions_failure");
            throw e;
        }
    }

    private void writeTransactionsToGoogleSheets(List<Transaction> transactions) {
        logger.info("Writing to Google Sheets...");

        try {
            // Clear sheets first
            googleSheetsService.clearSheet();

            // Write header
            googleSheetsService.writeHeader();

            // Append transactions
            googleSheetsService.appendTransactions(transactions);

            logger.info("Successfully wrote {} transactions to Google Sheets", transactions.size());
        } catch (IOException e) {
            logger.error("Failed to write to Google Sheets", e);
            throw new RuntimeException("Google Sheets write failed", e);
        }
    }

    @Test(priority = 3, dependsOnMethods = "extractTransactionsFromBanking")
    public void logoutFromBankingWebsite() {
        logger.info("Starting Logout...");

        try {
            // Click logout button
            logoutPage.clickLogoutButton();
        } catch (Exception e) {
            logger.error("Logout failed", e);
            takeScreenshot("logout_failure");
            throw e;
        }
    }

    @AfterClass
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
            automation.loginToBankingWebsite();
            automation.extractTransactionsFromBanking();
            automation.logoutFromBankingWebsite();
        } catch (Exception e) {
            logger.error("Automation failed", e);
        } finally {
            automation.tearDown();
        }
    }
}
