package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.AppConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

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

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(value);
    }

    protected void click(By locator) {
        WebElement element = clickable(locator);
        scrollIntoView(element);
        element.click();
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        return !driver.findElements(locator).isEmpty()
                && driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    protected void waitForText(By locator, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    protected boolean isTextPresent(By locator, String expectedText) {
        return !driver.findElements(locator).isEmpty()
                && driver.findElements(locator)
                .stream()
                .anyMatch(element -> element.isDisplayed() && element.getText().contains(expectedText));
    }

    protected void waitForUrlContaining(String partialUrl) {
        wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }
}
