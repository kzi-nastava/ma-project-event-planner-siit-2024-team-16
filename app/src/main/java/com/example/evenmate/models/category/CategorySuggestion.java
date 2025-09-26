package com.example.evenmate.models.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySuggestion {
    private Long id;
    private String name;
    private String description;
    private Long assetId;
    private String assetName;
    private String assetDescription;
}

