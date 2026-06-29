package com.incubyte.parabank.steps;

import com.incubyte.parabank.pages.AccountsOverviewPage;
import com.incubyte.parabank.pages.HomePage;
import com.incubyte.parabank.pages.RegisterPage;
import com.incubyte.parabank.utils.AppConfig;
import com.incubyte.parabank.utils.CredentialMasker;
import com.incubyte.parabank.utils.DriverManager;
import com.incubyte.parabank.utils.LoggerUtil;
import com.incubyte.parabank.utils.MoneyUtil;
import com.incubyte.parabank.utils.RegistrationData;
import com.incubyte.parabank.utils.ScenarioContext;
import com.incubyte.parabank.utils.TestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParaBankSteps {
    private static final List<String> EXPECTED_MANDATORY_FIELD_ERRORS = List.of(
            "First name is required.",
            "Last name is required.",
            "Address is required.",
            "City is required.",
            "State is required.",
            "Zip Code is required.",
            "Social Security Number is required.",
            "Username is required.",
            "Password is required.",
            "Password confirmation is required."
    );

    private final ScenarioContext scenarioContext = new ScenarioContext();
    private HomePage homePage;
    private RegisterPage registerPage;
    private AccountsOverviewPage accountsOverviewPage;

    private HomePage homePage() {
        if (homePage == null) {
            homePage = new HomePage(DriverManager.getDriver());
        }
        return homePage;
    }

    private RegisterPage registerPage() {
        if (registerPage == null) {
            registerPage = new RegisterPage(DriverManager.getDriver());
        }
        return registerPage;
    }

    private AccountsOverviewPage accountsOverviewPage() {
        if (accountsOverviewPage == null) {
            accountsOverviewPage = new AccountsOverviewPage(DriverManager.getDriver());
        }
        return accountsOverviewPage;
    }


    private RegistrationData registerNewUniqueUserWithRetry() {
        int maxAttempts = AppConfig.registrationRetryCount();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            RegistrationData user = TestData.validRegistrationData();
            LoggerUtil.info("Registering user attempt " + attempt + " of " + maxAttempts
                    + ": " + CredentialMasker.maskUsername(user.username()));

            homePage().goToRegister();
            registerPage().registerUser(user);

            boolean registrationSucceeded = registerPage().waitForSuccessfulRegistrationOrDuplicateUsername(user.username());
            if (registrationSucceeded) {
                LoggerUtil.info("Registration completed for user: " + CredentialMasker.maskUsername(user.username()));
                return user;
            }

            if (registerPage().isDuplicateUsernameErrorDisplayed()) {
                LoggerUtil.warn("Generated username already exists in ParaBank demo database: "
                        + CredentialMasker.maskUsername(user.username()) + ". Retrying with a new username.");
                homePage().open();
                continue;
            }

            throw new AssertionError("Registration did not complete and no duplicate username error was displayed. "
                    + "Current URL: " + DriverManager.getDriver().getCurrentUrl());
        }

        throw new AssertionError("Unable to register a unique ParaBank user after " + maxAttempts + " attempts.");
    }

    @Given("the user is on the ParaBank home page")
    public void iAmOnTheParaBankHomePage() {
        homePage().open();
        LoggerUtil.info("Opened ParaBank home page");
    }

    @When("the user register as a new ParaBank user")
    public void theUserRegisterAsANewParaBankUser() {
        RegistrationData user = registerNewUniqueUserWithRetry();
        scenarioContext.setCreatedUser(user);
    }

    @Then("the user should see successful registration message")
    public void iShouldSeeSuccessfulRegistrationMessage() {
        RegistrationData user = scenarioContext.getCreatedUser();

        String actualTitle = registerPage().getSuccessfulRegistrationTitle(user.username());
        String actualMessage = registerPage().getSuccessMessage();

        LoggerUtil.info("Registration success title: " + actualTitle);
        LoggerUtil.info("Registration success message: " + actualMessage);

        assertEquals("Unexpected registration success title", "Welcome " + user.username(), actualTitle);
        assertTrue("Unexpected registration success message",
                actualMessage.contains("Your account was created successfully"));
    }

    @When("the user logout from ParaBank")
    public void iLogoutFromParaBank() {
        accountsOverviewPage().logout();
        LoggerUtil.info("Logged out from ParaBank");
    }

    @And("the user login with the newly created user credentials")
    public void iLoginWithTheNewlyCreatedUserCredentials() {
        RegistrationData user = scenarioContext.getCreatedUser();

        LoggerUtil.info("Logging in with username: " + CredentialMasker.maskUsername(user.username())
                + " and password: " + CredentialMasker.maskPassword(user.password()));
        homePage().login(user.username(), user.password());
    }

    @Then("the user should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        assertTrue("Accounts overview page was not displayed", accountsOverviewPage().isLoaded());
        LoggerUtil.info("Accounts overview page displayed successfully");
    }

    @And("the user print the account amount displayed after login")
    public void iPrintTheAccountAmountDisplayedAfterLogin() {
        String amount = accountsOverviewPage().getFirstDisplayedAmount();
        scenarioContext.setAccountAmount(amount);

        BigDecimal parsedAmount = MoneyUtil.parseDollarAmount(amount);
        LoggerUtil.info("Amount displayed after login: " + amount);

        assertFalse("Displayed amount should not be empty", amount.isBlank());
        assertTrue("Displayed amount should be a valid dollar amount", parsedAmount.compareTo(BigDecimal.ZERO) >= 0);
    }

    @When("the user submit the registration form without mandatory details")
    public void iSubmitTheRegistrationFormWithoutMandatoryDetails() {
        homePage().goToRegister();
        registerPage().submitEmptyRegistrationForm();
    }

    @Then("the user should see registration validation errors")
    public void iShouldSeeRegistrationValidationErrors() {
        List<String> validationErrors = registerPage().getValidationErrors();
        scenarioContext.setValidationErrors(validationErrors);

        LoggerUtil.warn("Registration validation errors displayed: " + String.join(" | ", validationErrors));

        assertFalse("Expected mandatory field validation errors", validationErrors.isEmpty());
        assertTrue("Mandatory field validation errors were incomplete. Actual errors: " + validationErrors,
                validationErrors.containsAll(EXPECTED_MANDATORY_FIELD_ERRORS));
    }

    @And("the user try to register again with the same username")
    public void iTryToRegisterAgainWithTheSameUsername() {
        RegistrationData existingUser = scenarioContext.getCreatedUser();

        LoggerUtil.info("Trying duplicate registration for username: "
                + CredentialMasker.maskUsername(existingUser.username()));
        homePage().goToRegister();
        registerPage().registerUser(existingUser);
    }

    @Then("the user should see duplicate username error message")
    public void iShouldSeeDuplicateUsernameErrorMessage() {
        String duplicateUsernameError = registerPage().getUsernameError();
        LoggerUtil.warn("Duplicate username error displayed: " + duplicateUsernameError);

        assertTrue("Expected duplicate username error, but found: " + duplicateUsernameError,
                duplicateUsernameError.contains("This username already exists"));
    }

    @When("the user login with configured invalid credentials")
    public void iLoginWithConfiguredInvalidCredentials() {
        String username = AppConfig.invalidUsername();
        String password = AppConfig.invalidPassword();

        LoggerUtil.info("Attempting login with configured invalid username: " + CredentialMasker.maskUsername(username)
                + " and password: " + CredentialMasker.maskPassword(password));
        homePage().login(username, password);
    }

    @When("the user login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        LoggerUtil.info("Attempting login with username: " + CredentialMasker.maskUsername(username)
                + " and password: " + CredentialMasker.maskPassword(password));
        homePage().login(username, password);
    }

    @Then("the user should see login error message")
    public void iShouldSeeLoginErrorMessage() {
        String loginError = homePage().getLoginErrorMessage();
        LoggerUtil.warn("Invalid login error displayed: " + loginError);

        assertTrue("Expected invalid login error, but found: " + loginError,
                loginError.contains("The username and password could not be verified"));
    }
}
