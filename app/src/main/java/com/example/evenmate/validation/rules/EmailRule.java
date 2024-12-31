package com.example.evenmate.validation.rules;

import android.util.Patterns;
import com.example.evenmate.validation.ValidationRule;

public class EmailRule implements ValidationRule {
    @Override
    public boolean validate(String value) {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    @Override
    public String getErrorMessage() {
        return "is not valid";
    }
}