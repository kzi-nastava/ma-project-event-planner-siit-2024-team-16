package com.example.evenmate.validation.rules;

import com.example.evenmate.validation.ValidationRule;

public class OptionalMinLengthRule implements ValidationRule {
    private final int minLength;

    public OptionalMinLengthRule(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return value.length() >= minLength;
    }

    @Override
    public String getErrorMessage() {
        return "must be at least " + minLength + " characters long";
    }
}