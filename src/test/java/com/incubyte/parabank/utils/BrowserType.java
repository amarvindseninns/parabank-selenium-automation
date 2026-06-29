package com.incubyte.parabank.utils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }

        try {
            return BrowserType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            String supportedBrowsers = Arrays.stream(values())
                    .map(browser -> browser.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Unsupported browser: " + value
                    + ". Supported values are: " + supportedBrowsers, exception);
        }
    }
}
