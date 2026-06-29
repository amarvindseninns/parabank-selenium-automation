package com.incubyte.parabank.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class TestData {
    private TestData() {
    }

    public static String uniqueUsername() {
    //        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    //        return "auto_user_" + timestamp;
        return "testuser" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static RegistrationData validRegistrationData() {
        String username = uniqueUsername();
        String password = "Password@123";

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
