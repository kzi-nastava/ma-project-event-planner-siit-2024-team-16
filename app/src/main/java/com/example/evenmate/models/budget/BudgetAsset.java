package com.example.evenmate.models.budget;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetAsset {
    private Long id;
    private Long assetId;
    private String name;
    private Double price;
    private String type; // "PURCHASE" / "SERVICE"
}

