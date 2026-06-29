package com.incubyte.parabank.utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    private static WebDriver createDriver() {
        BrowserType browser = AppConfig.browser();
        boolean headless = AppConfig.headless();
        String windowSize = AppConfig.windowWidth() + "," + AppConfig.windowHeight();

        LoggerUtil.info("Creating WebDriver session for browser=" + browser.name().toLowerCase()
                + ", headless=" + headless);

        WebDriver driver;
        switch (browser) {
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setAcceptInsecureCerts(AppConfig.acceptInsecureCerts());
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addArguments("--width=" + AppConfig.windowWidth());
                options.addArguments("--height=" + AppConfig.windowHeight());
                driver = new FirefoxDriver(options);
            }
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                options.setAcceptInsecureCerts(AppConfig.acceptInsecureCerts());
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=" + windowSize);
                options.addArguments("--disable-notifications");
                options.addArguments("--remote-allow-origins=*");
                driver = new EdgeDriver(options);
            }
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                options.setAcceptInsecureCerts(AppConfig.acceptInsecureCerts());
                options.setExperimentalOption("prefs", Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false
                ));
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=" + windowSize);
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(AppConfig.implicitWait());
        driver.manage().timeouts().pageLoadTimeout(AppConfig.pageLoadTimeout());

        if (AppConfig.maximizeWindow() && !headless) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(new Dimension(AppConfig.windowWidth(), AppConfig.windowHeight()));
        }

        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
