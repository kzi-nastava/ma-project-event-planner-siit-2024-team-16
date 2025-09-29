package com.example.evenmate.models.budget;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetItem {
    private Long id;
    private Double amount;
    private Long categoryId;
    private String categoryName;
}

