package com.incubyte.parabank.hooks;

import com.incubyte.parabank.utils.DriverFactory;
import com.incubyte.parabank.utils.LoggerUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {
    @Before
    public void setUp(Scenario scenario) {
        LoggerUtil.info("Starting scenario: " + scenario.getName());
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();

        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
            LoggerUtil.warn("Scenario failed: " + scenario.getName());
        } else {
            LoggerUtil.info("Scenario passed: " + scenario.getName());
        }

        DriverFactory.quitDriver();
    }
}
