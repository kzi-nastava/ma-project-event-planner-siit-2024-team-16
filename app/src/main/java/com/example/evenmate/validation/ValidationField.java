package com.example.evenmate.validation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ValidationField {
    private final TextInputLayout layout;
    private final TextInputEditText input;
    private final String fieldName;
    private final ValidationRule[] rules;

    public ValidationField(TextInputLayout layout, TextInputEditText input, String fieldName, ValidationRule... rules) {
        this.layout = layout;
        this.input = input;
        this.fieldName = fieldName;
        this.rules = rules;
    }

    public TextInputLayout getLayout() {
        return layout;
    }

    public TextInputEditText getInput() {
        return input;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ValidationRule[] getRules() {
        return rules;
    }
}