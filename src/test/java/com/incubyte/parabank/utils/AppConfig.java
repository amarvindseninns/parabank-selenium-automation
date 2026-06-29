package com.incubyte.parabank.utils;

import java.time.Duration;

public final class AppConfig {
    private static final String DEFAULT_BASE_URL = "https://parabank.parasoft.com/parabank/index.htm?ConnType=JDBC";

    private AppConfig() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return System.getProperty("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }

    public static Duration explicitWait() {
        long seconds = Long.parseLong(System.getProperty("explicitWait", "15"));
        return Duration.ofSeconds(seconds);
    }

    public static Duration implicitWait() {
        long seconds = Long.parseLong(System.getProperty("implicitWait", "2"));
        return Duration.ofSeconds(seconds);
    }
}
