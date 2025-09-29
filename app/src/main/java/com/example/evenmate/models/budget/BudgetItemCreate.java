package com.example.evenmate.models.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItemCreate {
    private Double amount;
    private Long eventId;
    private Long categoryId;
}
