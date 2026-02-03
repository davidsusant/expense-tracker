package com.expense.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogoutPage extends BasePage {

    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/ul/li[3]/a")
    private WebElement logoutButton;
    
    public LogoutPage(WebDriver driver) {
        super(driver);
    }

    public void logout() {
        clickLogoutButton();
        logger.info("Logout completed");
    }

    public void clickLogoutButton() {
        click(logoutButton);
        logger.info("Clicked logout button");
    }
}
