package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.RegistrationData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import java.util.stream.Collectors;

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

    public void registerUser(RegistrationData data) {
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
        click(registerButton);
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

    public boolean hasValidationErrors() {
        visible(validationErrors);
        return driver.findElements(validationErrors).stream().anyMatch(element -> element.isDisplayed());
    }

    public List<String> getValidationErrors() {
        visible(validationErrors);
        return driver.findElements(validationErrors)
                .stream()
                .filter(element -> element.isDisplayed() && !element.getText().isBlank())
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    public String getUsernameError() {
        return text(usernameError);
    }
}
