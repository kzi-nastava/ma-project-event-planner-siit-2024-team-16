package com.example.evenmate.validation.rules;

import com.example.evenmate.validation.ValidationRule;
import com.google.android.material.textfield.TextInputEditText;

public class RequiredOldPasswordRule implements ValidationRule {
    private final TextInputEditText newPasswordField;

    public RequiredOldPasswordRule(TextInputEditText newPasswordField) {
        this.newPasswordField = newPasswordField;
    }

    @Override
    public boolean validate(String value) {
        if(newPasswordField.getText() == null || newPasswordField.getText().toString().isEmpty())
            return true;
        return !value.isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "is required";
    }
}
