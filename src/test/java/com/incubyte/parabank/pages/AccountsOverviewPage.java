package com.incubyte.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AccountsOverviewPage extends BasePage {
    private final By accountsOverviewTitle = By.cssSelector("#rightPanel h1.title");
    private final By logoutLink = By.linkText("Log Out");
    private final By accountTable = By.id("accountTable");
    private final By amountCells = By.cssSelector("#accountTable tbody tr td:nth-child(2)");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        waitForText(accountsOverviewTitle, "Accounts Overview");
        return text(accountsOverviewTitle).equalsIgnoreCase("Accounts Overview");
    }

    public void logout() {
        click(logoutLink);
    }

    public String getDisplayedAmount() {
        visible(accountTable);
        List<WebElement> amounts = driver.findElements(amountCells);
        return amounts.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(value -> value.startsWith("$"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No account amount was displayed on Accounts Overview page"));
    }
}
