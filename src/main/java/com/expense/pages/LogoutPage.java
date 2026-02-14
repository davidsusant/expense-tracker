package com.expense.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogoutPage extends BasePage {

    // BCA Website Element
    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/ul/li[3]/a")
    private WebElement bcaLogoutButton;

    @FindBy(xpath = "//*[@id=\"main-navigation\"]/div[2]/button")
    private WebElement cimbLogoutButton;
    
    public LogoutPage(WebDriver driver) {
        super(driver);
    }

    public void bcaLogout() {
        clickLogoutButton(bcaLogoutButton);
        logger.info("Logout from BCA banking website completed");
    }

    public void cimbLogout() {
        clickLogoutButton(cimbLogoutButton);
        logger.info("Logout from CIMB banking website completed");
    }

    public void clickLogoutButton(WebElement logoutButton) {
        click(logoutButton);
        logger.info("Clicked logout button");
    }
}
