package com.incubyte.parabank.utils;

public final class CredentialMasker {
    private CredentialMasker() {
    }

    public static String maskUsername(String username) {
        if (username == null || username.isBlank()) {
            return "<empty>";
        }

        String trimmed = username.trim();
        if (trimmed.length() <= 4) {
            return "****";
        }

        String prefix = trimmed.substring(0, Math.min(3, trimmed.length()));
        String suffix = trimmed.substring(trimmed.length() - Math.min(3, trimmed.length()));
        return prefix + "********" + suffix;
    }

    public static String maskPassword(String password) {
        if (password == null || password.isBlank()) {
            return "<empty>";
        }
        return "********";
    }
}
