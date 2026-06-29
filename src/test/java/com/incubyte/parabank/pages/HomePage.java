package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.AppConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By registerLink = By.linkText("Register");
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("input[value='Log In']");
    private final By errorMessage = By.cssSelector("#rightPanel .error");
    private final By customerLoginTitle = By.cssSelector("#leftPanel h2");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(AppConfig.baseUrl());
        waitUntilLoaded();
    }

    public void waitUntilLoaded() {
        visible(customerLoginTitle);
        visible(usernameInput);
        visible(passwordInput);
    }

    public void goToRegister() {
        click(registerLink);
        waitForUrlContaining("register");
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }

    public String getLoginErrorMessage() {
        return text(errorMessage);
    }
}
