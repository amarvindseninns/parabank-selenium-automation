package com.incubyte.parabank.utils;

public final class ScenarioContext {
    private RegistrationData createdUser;

    public RegistrationData getCreatedUser() {
        if (createdUser == null) {
            throw new IllegalStateException("No user has been created in this scenario yet.");
        }
        return createdUser;
    }

    public void setCreatedUser(RegistrationData createdUser) {
        this.createdUser = createdUser;
    }
}
