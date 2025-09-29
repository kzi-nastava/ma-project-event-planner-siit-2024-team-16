package com.example.evenmate.validation.rules;

import com.example.evenmate.validation.ValidationRule;

public class RequiredRule implements ValidationRule {
    @Override
    public boolean validate(String value) {
        return !value.isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "is required";
    }
}
