package com.incubyte.parabank.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class TestData {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter USERNAME_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("MMddHHmmssSSS");

    private TestData() {
    }

    public static String uniqueUsername() {

        String uuId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "TestUser_"+uuId;

    }

    public static RegistrationData validRegistrationData() {
        String username = uniqueUsername();
        String password = AppConfig.defaultPassword();

        return new RegistrationData(
                "Automation",
                "Tester",
                "123 Test Street",
                "Bengaluru",
                "Karnataka",
                "560001",
                "9999999999",
                "123-45-6789",
                username,
                password
        );
    }
}
