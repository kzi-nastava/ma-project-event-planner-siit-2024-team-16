package com.example.evenmate.validation.rules;

import com.example.evenmate.validation.ValidationRule;

public class MinLengthRule implements ValidationRule {
    private final int minLength;

    public MinLengthRule(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean validate(String value) {
        return value.length() >= minLength;
    }

    @Override
    public String getErrorMessage() {
        return "must be at least " + minLength + " characters long";
    }
}
