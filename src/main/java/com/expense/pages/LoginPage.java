package com.expense.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/main/ng-component/section/div/div[2]/app-card/app-card-body/form/div[4]/div/button")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLoginPage(String url) {
        driver.get(url);
        waitForPageLoad();
        logger.info("Navigated to login page: {}", url);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        waitForPageLoad();
        logger.info("Login completed");
    }

    public void enterUsername(String username) {
        sendKeys(usernameField, username);
        logger.info("Entered username");
    }

    public void enterPassword(String password) {
        sendKeys(passwordField, password);
        logger.info("Entered password");
    }

    public void clickLoginButton() {
        click(loginButton);
        logger.info("Clicked login button");
    }
}
