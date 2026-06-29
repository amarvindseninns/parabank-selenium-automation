package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.RegistrationData;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class RegisterPage extends BasePage {
    private final By firstName = By.id("customer.firstName");
    private final By lastName = By.id("customer.lastName");
    private final By address = By.id("customer.address.street");
    private final By city = By.id("customer.address.city");
    private final By state = By.id("customer.address.state");
    private final By zipCode = By.id("customer.address.zipCode");
    private final By phone = By.id("customer.phoneNumber");
    private final By ssn = By.id("customer.ssn");
    private final By username = By.id("customer.username");
    private final By password = By.id("customer.password");
    private final By confirmPassword = By.id("repeatedPassword");
    private final By registerButton = By.cssSelector("input[value='Register']");
    private final By pageTitle = By.cssSelector("#rightPanel h1.title");
    private final By successMessage = By.cssSelector("#rightPanel p");
    private final By validationErrors = By.cssSelector("#customerForm .error");
    private final By usernameError = By.id("customer.username.errors");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        waitForText(pageTitle, "Signing up is easy!");
        visible(firstName);
        visible(registerButton);
    }

    public void registerUser(RegistrationData data) {
        waitUntilLoaded();
        type(firstName, data.firstName());
        type(lastName, data.lastName());
        type(address, data.address());
        type(city, data.city());
        type(state, data.state());
        type(zipCode, data.zipCode());
        type(phone, data.phone());
        type(ssn, data.ssn());
        type(username, data.username());
        type(password, data.password());
        type(confirmPassword, data.password());
        click(registerButton);
    }

    public void submitEmptyRegistrationForm() {
        waitUntilLoaded();
        click(registerButton);
    }

    public boolean waitForSuccessfulRegistrationOrDuplicateUsername(String expectedUsername) {
        String expectedTitle = "Welcome " + expectedUsername;
        try {
            return wait.until(webDriver -> {
                if (isTextPresent(pageTitle, expectedTitle)) {
                    return true;
                }

                if (isDuplicateUsernameErrorDisplayed()) {
                    return false;
                }

                return null;
            });
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public String getSuccessfulRegistrationTitle(String expectedUsername) {
        String expectedTitle = "Welcome " + expectedUsername;
        waitForText(pageTitle, expectedTitle);
        return text(pageTitle);
    }

    public String getSuccessMessage() {
        waitForText(successMessage, "Your account was created successfully");
        return text(successMessage);
    }

    public List<String> getValidationErrors() {
        return allVisible(validationErrors)
                .stream()
                .filter(element -> !element.getText().isBlank())
                .map(element -> element.getText().trim())
                .toList();
    }

    public boolean isDuplicateUsernameErrorDisplayed() {
        return driver.findElements(usernameError)
                .stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .anyMatch(message -> message.contains("This username already exists"));
    }

    public String getUsernameError() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameError));
        return text(usernameError);
    }
}
