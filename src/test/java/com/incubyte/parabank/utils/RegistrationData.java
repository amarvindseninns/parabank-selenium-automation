package com.incubyte.parabank.utils;

public record RegistrationData(
        String firstName,
        String lastName,
        String address,
        String city,
        String state,
        String zipCode,
        String phone,
        String ssn,
        String username,
        String password
) {
}
