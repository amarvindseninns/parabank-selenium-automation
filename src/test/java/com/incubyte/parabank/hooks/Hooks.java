package com.incubyte.parabank.hooks;

import com.incubyte.parabank.utils.AppConfig;
import com.incubyte.parabank.utils.DriverManager;
import com.incubyte.parabank.utils.FileUtil;
import com.incubyte.parabank.utils.LoggerUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {
    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        LoggerUtil.info("Starting scenario: " + scenario.getName());
        LoggerUtil.info("Runtime config: " + AppConfig.safeConfigSummary());
        FileUtil.createDirectory(AppConfig.artifactsDir());
        DriverManager.getDriver();
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        WebDriver driver = DriverManager.hasDriver() ? DriverManager.getDriver() : null;

        try {
            if (scenario.isFailed()) {
                if (driver != null) {
                    captureFailureEvidence(driver, scenario);
                }
                LoggerUtil.warn("Scenario failed: " + scenario.getName());
            } else {
                LoggerUtil.info("Scenario passed: " + scenario.getName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

    private void captureFailureEvidence(WebDriver driver, Scenario scenario) {
        String safeScenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]+", "_").toLowerCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        Path scenarioArtifactDir = AppConfig.artifactsDir().resolve(safeScenarioName + "_" + timestamp);
        FileUtil.createDirectory(scenarioArtifactDir);

        if (AppConfig.screenshotOnFailure() && driver instanceof TakesScreenshot takesScreenshot) {
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");

            Path screenshotPath = scenarioArtifactDir.resolve("failure-screenshot.png");
            try {
                Files.write(screenshotPath, screenshot);
                LoggerUtil.info("Failure screenshot saved at: " + screenshotPath);
            } catch (Exception exception) {
                LoggerUtil.error("Unable to save failure screenshot", exception);
            }
        }

        if (AppConfig.pageSourceOnFailure()) {
            Path pageSourcePath = scenarioArtifactDir.resolve("page-source.html");
            FileUtil.writeTextFile(pageSourcePath, driver.getPageSource());
            scenario.attach(driver.getPageSource(), "text/html", "failure-page-source");
            LoggerUtil.info("Failure page source saved at: " + pageSourcePath);
        }
    }
}
