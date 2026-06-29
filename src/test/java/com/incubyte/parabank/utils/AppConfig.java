package com.incubyte.parabank.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

public final class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new FrameworkException(CONFIG_FILE + " was not found under src/test/resources");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new FrameworkException("Unable to load " + CONFIG_FILE, exception);
        }

        return properties;
    }

    private static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (hasText(systemValue)) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv(toEnvironmentVariableName(key));
        if (hasText(environmentValue)) {
            return environmentValue.trim();
        }

        String propertyValue = PROPERTIES.getProperty(key);
        if (hasText(propertyValue)) {
            return propertyValue.trim();
        }

        return defaultValue;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String toEnvironmentVariableName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
    }

    private static int getInt(String key, int defaultValue) {
        String value = get(key, String.valueOf(defaultValue));
        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < 0) {
                throw new IllegalArgumentException("Property '" + key + "' must not be negative: " + value);
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer value for property '" + key + "': " + value, exception);
        }
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, String.valueOf(defaultValue)).toLowerCase(Locale.ROOT);
        if (!value.equals("true") && !value.equals("false")) {
            throw new IllegalArgumentException("Invalid boolean value for property '" + key + "': " + value
                    + ". Supported values are true or false.");
        }
        return Boolean.parseBoolean(value);
    }

    public static String env() {
        return get("env", "qa").toLowerCase(Locale.ROOT);
    }

    public static String baseUrl() {
        String commandLineBaseUrl = System.getProperty("base.url");
        if (!hasText(commandLineBaseUrl)) {
            commandLineBaseUrl = System.getProperty("baseUrl");
        }

        if (hasText(commandLineBaseUrl)) {
            return commandLineBaseUrl.trim();
        }

        String environmentBaseUrl = System.getenv("BASE_URL");
        if (hasText(environmentBaseUrl)) {
            return environmentBaseUrl.trim();
        }

        String envBaseUrlKey = env() + ".base.url";
        String envBaseUrl = PROPERTIES.getProperty(envBaseUrlKey);
        if (!hasText(envBaseUrl)) {
            throw new IllegalArgumentException("Base URL is not configured for env='" + env()
                    + "'. Expected property: " + envBaseUrlKey);
        }

        return envBaseUrl.trim();
    }

    public static BrowserType browser() {
        return BrowserType.from(get("browser", "chrome"));
    }

    public static boolean headless() {
        return getBoolean("headless", false);
    }

    public static boolean maximizeWindow() {
        return getBoolean("maximize.window", true);
    }

    public static int windowWidth() {
        return getInt("window.width", 1440);
    }

    public static int windowHeight() {
        return getInt("window.height", 900);
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(getInt("explicit.wait.seconds", 15));
    }

    public static Duration implicitWait() {
        return Duration.ofSeconds(getInt("implicit.wait.seconds", 0));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(getInt("page.load.timeout.seconds", 30));
    }

    public static Duration accountDataWait() {
        return Duration.ofSeconds(getInt("account.data.timeout.seconds", 45));
    }

    public static int registrationRetryCount() {
        int retryCount = getInt("registration.retry.count", 3);
        if (retryCount == 0) {
            throw new IllegalArgumentException("Property 'registration.retry.count' must be greater than 0");
        }
        return retryCount;
    }

    public static String defaultPassword() {
        return get("default.password", "Password@123");
    }

    public static String invalidUsername() {
        return get("invalid.username", "invalid_user345");
    }

    public static String invalidPassword() {
            return get("invalid.password", "wrongPassword");
    }

    public static boolean screenshotOnFailure() {
        return getBoolean("screenshot.on.failure", true);
    }

    public static boolean pageSourceOnFailure() {
        return getBoolean("page.source.on.failure", true);
    }

    public static Path artifactsDir() {
        return Path.of(get("artifacts.dir", "target/test-artifacts"));
    }

    public static boolean acceptInsecureCerts() {
        return getBoolean("accept.insecure.certs", true);
    }

    public static String safeConfigSummary() {
        return "env=" + env()
                + ", browser=" + browser().name().toLowerCase(Locale.ROOT)
                + ", headless=" + headless()
                + ", baseUrl=" + baseUrl()
                + ", implicitWait=" + implicitWait().toSeconds() + "s"
                + ", explicitWait=" + explicitWait().toSeconds() + "s"
                + ", artifactsDir=" + artifactsDir();
    }
}
