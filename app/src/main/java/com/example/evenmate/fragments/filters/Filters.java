package com.example.evenmate.fragments.filters;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filters {
    private Double minRating;
    private Double maxRating;
    private String dateFrom;
    private String dateTo;
    private List<Long> selectedTypes;
    private List<String> selectedLocations;
    private boolean showInPast;
}

