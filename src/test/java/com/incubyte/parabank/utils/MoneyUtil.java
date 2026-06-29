package com.incubyte.parabank.utils;

import java.math.BigDecimal;

public final class MoneyUtil {
    private MoneyUtil() {
    }

    public static BigDecimal parseDollarAmount(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Amount value is blank");
        }

        String normalized = value.trim().replace("$", "").replace(",", "");
        return new BigDecimal(normalized);
    }
}
