package com.incubyte.parabank.steps;

import com.incubyte.parabank.pages.AccountsOverviewPage;
import com.incubyte.parabank.pages.HomePage;
import com.incubyte.parabank.pages.RegisterPage;
import com.incubyte.parabank.utils.CredentialMasker;
import com.incubyte.parabank.utils.DriverFactory;
import com.incubyte.parabank.utils.LoggerUtil;
import com.incubyte.parabank.utils.RegistrationData;
import com.incubyte.parabank.utils.ScenarioContext;
import com.incubyte.parabank.utils.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParaBankSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final ScenarioContext scenarioContext = new ScenarioContext();
    private final HomePage homePage = new HomePage(driver);
    private final RegisterPage registerPage = new RegisterPage(driver);
    private final AccountsOverviewPage accountsOverviewPage = new AccountsOverviewPage(driver);

    @Given("I am on the ParaBank home page")
    public void iAmOnTheParaBankHomePage() {
        homePage.open();
    }

    @When("I register as a new ParaBank user")
    public void iRegisterAsANewParaBankUser() {
        RegistrationData user = TestData.validRegistrationData();
        scenarioContext.setCreatedUser(user);

        LoggerUtil.info("Registering user: " + CredentialMasker.maskUsername(user.username()));
        homePage.goToRegister();
        registerPage.registerUser(user);
    }

    @Then("I should see successful registration message")
    public void iShouldSeeSuccessfulRegistrationMessage() {
        RegistrationData user = scenarioContext.getCreatedUser();

        String actualTitle = registerPage.getSuccessfulRegistrationTitle(user.username());
        String actualMessage = registerPage.getSuccessMessage();

        LoggerUtil.info("Registration success title: " + actualTitle);
        LoggerUtil.info("Registration success message: " + actualMessage);

        assertEquals("Welcome " + user.username(), actualTitle);
        assertTrue(actualMessage.contains("Your account was created successfully"));
    }

    @When("I logout from ParaBank")
    public void iLogoutFromParaBank() {
        accountsOverviewPage.logout();
    }

    @And("I login with the newly created user credentials")
    public void iLoginWithTheNewlyCreatedUserCredentials() {
        RegistrationData user = scenarioContext.getCreatedUser();

        LoggerUtil.info("Logging in with username: " + CredentialMasker.maskUsername(user.username())
                + " and password: " + CredentialMasker.maskPassword(user.password()));
        homePage.login(user.username(), user.password());
    }

    @Then("I should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        assertTrue("Accounts overview page was not displayed", accountsOverviewPage.isLoaded());
    }

    @And("I print the account amount displayed after login")
    public void iPrintTheAccountAmountDisplayedAfterLogin() {
        String amount = accountsOverviewPage.getDisplayedAmount();
        LoggerUtil.info("Amount displayed after login: " + amount);
        assertFalse("Displayed amount should not be empty", amount.isBlank());
    }

    @When("I submit the registration form without mandatory details")
    public void iSubmitTheRegistrationFormWithoutMandatoryDetails() {
        homePage.goToRegister();
        registerPage.submitEmptyRegistrationForm();
    }

    @Then("I should see registration validation errors")
    public void iShouldSeeRegistrationValidationErrors() {
        List<String> validationErrors = registerPage.getValidationErrors();
        LoggerUtil.warn("Registration validation errors displayed: " + String.join(" | ", validationErrors));

        assertFalse("Expected mandatory field validation errors", validationErrors.isEmpty());
    }

    @And("I try to register again with the same username")
    public void iTryToRegisterAgainWithTheSameUsername() {
        RegistrationData existingUser = scenarioContext.getCreatedUser();

        LoggerUtil.info("Trying duplicate registration for username: "
                + CredentialMasker.maskUsername(existingUser.username()));
        homePage.goToRegister();
        registerPage.registerUser(existingUser);
    }

    @Then("I should see duplicate username error message")
    public void iShouldSeeDuplicateUsernameErrorMessage() {
        String duplicateUsernameError = registerPage.getUsernameError();
        LoggerUtil.warn("Duplicate username error displayed: " + duplicateUsernameError);

        assertTrue(duplicateUsernameError.contains("This username already exists"));
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        LoggerUtil.info("Attempting login with username: " + CredentialMasker.maskUsername(username)
                + " and password: " + CredentialMasker.maskPassword(password));
        homePage.login(username, password);
    }

    @Then("I should see login error message")
    public void iShouldSeeLoginErrorMessage() {
        String loginError = homePage.getLoginErrorMessage();
        LoggerUtil.warn("Invalid login error displayed: " + loginError);

        assertTrue(loginError.contains("The username and password could not be verified"));
    }
}
