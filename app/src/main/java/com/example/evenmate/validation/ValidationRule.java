package com.example.evenmate.validation;

public interface ValidationRule {
    boolean validate(String value);
    String getErrorMessage();
}