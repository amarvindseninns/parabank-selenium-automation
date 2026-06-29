package com.incubyte.parabank.utils;

import java.util.ArrayList;
import java.util.List;

public final class ScenarioContext {
    private RegistrationData createdUser;
    private String accountAmount;
    private List<String> validationErrors = new ArrayList<>();

    public RegistrationData getCreatedUser() {
        if (createdUser == null) {
            throw new IllegalStateException("No user has been created in this scenario yet.");
        }
        return createdUser;
    }

    public void setCreatedUser(RegistrationData createdUser) {
        this.createdUser = createdUser;
    }

    public String getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(String accountAmount) {
        this.accountAmount = accountAmount;
    }

    public List<String> getValidationErrors() {
        return List.copyOf(validationErrors);
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    public void clear() {
        createdUser = null;
        accountAmount = null;
        validationErrors.clear();
    }
}
