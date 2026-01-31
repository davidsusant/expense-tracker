package com.expense;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.expense.pages.LoginPage;
import com.expense.utils.ConfigReader;
import com.expense.utils.DriverManager;

public class ExpenseTrackerAutomation {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTrackerAutomation.class);

    private WebDriver driver;

    private LoginPage loginPage;

    @BeforeClass
    public void setup() {
        logger.info("Starting Expense Tracker Automation...");

        try {
            // Initialize WebDriver
            driver = DriverManager.getDriver();

            // Initialize Page Objects
            loginPage = new LoginPage(driver);

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
        } catch (Exception e) {
            logger.error("Automation failed", e);
        } finally {
            automation.tearDown();
        }
    }
}
