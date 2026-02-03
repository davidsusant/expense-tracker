package com.expense.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TransactionsPage extends BasePage {

    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/app-nav-menu/ul/li[3]/a")
    private WebElement myAccountMenuLink;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/app-header/header/nav/div/div/app-nav-menu/ul/li[3]/div/ul/li[2]/a")
    private WebElement creditCardInformationMenuLink;

    @FindBy(xpath = "/html/body/app-root/ng-component/section/section[2]/ng-component/main/div/div/div[1]/app-side-menu/ul[2]/a[2]")
    private WebElement unbilledTransactionsMenuLink;

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
}
