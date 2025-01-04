package com.example.evenmate.validation.rules;

import com.google.android.material.textfield.TextInputEditText;
import com.example.evenmate.validation.ValidationRule;

import java.util.Objects;

public class MatchPasswordRule implements ValidationRule {
    private final TextInputEditText passwordField;

    public MatchPasswordRule(TextInputEditText passwordField) {
        this.passwordField = passwordField;
    }

    @Override
    public boolean validate(String value) {
        return value.equals(Objects.requireNonNull(passwordField.getText()).toString());
    }

    @Override
    public String getErrorMessage() {
        return "passwords do not match";
    }
}
