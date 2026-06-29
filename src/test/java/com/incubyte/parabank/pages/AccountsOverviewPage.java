package com.incubyte.parabank.pages;

import com.incubyte.parabank.utils.AppConfig;
import com.incubyte.parabank.utils.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AccountsOverviewPage extends BasePage {
    private final By accountsOverviewTitle = By.cssSelector("#rightPanel h1.title");
    private final By accountsOverviewLink = By.linkText("Accounts Overview");
    private final By logoutLink = By.linkText("Log Out");
    private final By accountTable = By.id("accountTable");
    private final By accountRows = By.cssSelector("#accountTable tbody tr");
    private final By balanceAmountCells = By.cssSelector("#accountTable tbody tr td:nth-child(2)");
    private final By availableAmountCells = By.cssSelector("#accountTable tbody tr td:nth-child(3)");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        waitForText(accountsOverviewTitle, "Accounts Overview");
        visible(accountTable);
        return text(accountsOverviewTitle).equalsIgnoreCase("Accounts Overview");
    }

    public void open() {
        click(accountsOverviewLink);
        waitForText(accountsOverviewTitle, "Accounts Overview");
        visible(accountTable);
    }

    public void logout() {
        click(logoutLink);
        waitForUrlContaining("index");
    }

    public List<String> getDisplayedAmounts() {
        waitForAccountAmountToBeDisplayed();
        return readDisplayedAmounts();
    }

    public String getFirstDisplayedAmount() {
        return getDisplayedAmounts().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError(buildNoAmountErrorMessage()));
    }

    private void waitForAccountAmountToBeDisplayed() {
        try {
            WebDriverWait accountDataWait = new WebDriverWait(driver, AppConfig.accountDataWait());
            accountDataWait.pollingEvery(Duration.ofSeconds(2));

            accountDataWait.until(webDriver -> {
                waitForText(accountsOverviewTitle, "Accounts Overview");
                visible(accountTable);

                List<String> amounts = readDisplayedAmounts();
                if (!amounts.isEmpty()) {
                    LoggerUtil.info("Account amount displayed on Accounts Overview page: " + amounts.get(0));
                    return true;
                }

                LoggerUtil.warn("Account amount is not displayed yet. Account table rows found: "
                        + driver.findElements(accountRows).size()
                        + ". Re-opening Accounts Overview and retrying.");
                reopenAccountsOverviewSafely();
                return false;
            });
        } catch (TimeoutException exception) {
            throw new AssertionError(buildNoAmountErrorMessage(), exception);
        }
    }

    private void reopenAccountsOverviewSafely() {
        if (!driver.findElements(accountsOverviewLink).isEmpty()) {
            driver.findElement(accountsOverviewLink).click();
        } else {
            driver.navigate().refresh();
        }
    }

    private List<String> readDisplayedAmounts() {
        visible(accountTable);
        List<String> balanceAmounts = readDollarAmounts(balanceAmountCells);
        if (!balanceAmounts.isEmpty()) {
            return balanceAmounts;
        }
        return readDollarAmounts(availableAmountCells);
    }

    private List<String> readDollarAmounts(By amountCells) {
        return driver.findElements(amountCells)
                .stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(value -> value.matches("^\\$\\d+(?:,\\d{3})*(?:\\.\\d{2})?$"))
                .toList();
    }

    private String buildNoAmountErrorMessage() {
        String tableText = driver.findElements(accountTable).isEmpty()
                ? "Account table was not present"
                : driver.findElement(accountTable).getText().replaceAll("\\s+", " ").trim();

        return "No account amount was displayed on Accounts Overview page within "
                + AppConfig.accountDataWait().toSeconds()
                + " seconds. Current URL: " + driver.getCurrentUrl()
                + ". Account rows found: " + driver.findElements(accountRows).size()
                + ". Account table text: [" + tableText + "].";
    }
}
