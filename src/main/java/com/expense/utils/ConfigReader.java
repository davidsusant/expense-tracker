package com.expense.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configguration file: " + CONFIG_FILE_PATH);
        }
    }

    public static String getBrowser() {
        return properties.getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless"));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(properties.getProperty("page.load.timeout"));
    }

    public static String getScreenshotPath() {
        return properties.getProperty("screenshot.path");
    }

    // BCA Banking Website
    public static String getBankingUrl() {
        return properties.getProperty("bca.banking.url");
    }

    public static String getBankingUsername() {
        return properties.getProperty("bca.banking.username");
    }

    public static String getBankingPassword() {
        return properties.getProperty("bca.banking.password");
    }

    // CIMB Banking Website
    public static String getCimbBankingUrl() {
        return properties.getProperty("cimb.banking.url");
    }

    public static String getCimbBankingUsername() {
        return properties.getProperty("cimb.banking.username");
    }

    public static String getCimbBankingPassword() {
        return properties.getProperty("cimb.banking.password");
    }

    public static String getGoogleCredentialsPath() {
        return properties.getProperty("google.credentials.path");
    }

    public static String getSpreadsheetId() {
        return properties.getProperty("google.spreadsheet.id");
    }

    public static String getBcaSheetName() {
        return properties.getProperty("bca.google.sheet.name");
    }

    public static String getCimbSheetName() {
        return properties.getProperty("cimb.google.sheet.name");
    }

    public static String getApplicationName() {
        return properties.getProperty("google.application.name");
    }
}
