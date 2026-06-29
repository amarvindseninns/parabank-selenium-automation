package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.AppConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, AppConfig.explicitWait());
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected void waitForText(By locator, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }
}
