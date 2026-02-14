package com.expense.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(name = "username")
    private WebElement bcaUsernameField;

    @FindBy(name = "password")
    private WebElement bcaPasswordField;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/main/ng-component/section/div/div[2]/app-card/app-card-body/form/div[4]/div/button")
    private WebElement bcaLoginButton;

    // CIMB Login Page Element
    @FindBy(id = "username")
    private WebElement cimbUsernameField;

    @FindBy(id = "password")
    private WebElement cimbPasswordField;

    @FindBy(xpath = "//*[@id=\"root\"]/div[2]/div/div[2]/div/div/form/button[2]")
    private WebElement cimbLoginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // BCA Login Page
    public void navigateToBcaLoginPage(String url) {
        driver.get(url);
        waitForPageLoad();
        logger.info("Navigated to login page: {}", url);
    }

    public void bcaLogin(String username, String password) {
        enterUsername(bcaUsernameField, username);
        enterPassword(bcaPasswordField, password);
        clickLoginButton(bcaLoginButton);
        waitForPageLoad();
        logger.info("BCA website login completed");
    }

    // CIMB Login Page
    public void navigateToCimbLoginPage(String url) {
        driver.get(url);
        waitForPageLoad();
        logger.info("Navigated to CIMB login page: {}", url);
    }

    public void cimbLogin(String username, String password) {
        enterUsername(cimbUsernameField, username);
        enterPassword(cimbPasswordField, password);
        clickLoginButton(cimbLoginButton);
        waitForPageLoad();
        logger.info("CIMB website login completed");
    }

    public void enterUsername(WebElement usernameField, String username) {
        sendKeys(usernameField, username);
        logger.info("Entered username");
    }

    public void enterPassword(WebElement passwordField, String password) {
        sendKeys(passwordField, password);
        logger.info("Entered password");
    }

    public void clickLoginButton(WebElement loginButton) {
        click(loginButton);
        logger.info("Clicked login button");
    }

    
}
