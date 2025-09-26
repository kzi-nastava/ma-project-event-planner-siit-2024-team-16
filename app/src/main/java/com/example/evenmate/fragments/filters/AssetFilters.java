package com.example.evenmate.fragments.filters;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetFilters extends Filters{
    private Double minPrice;
    private Double maxPrice;
    private List<Long> selectedCategories;
    private List<Long> selectedProviders;
    private boolean showServicesOnly;
    private boolean showProductsOnly;
}
