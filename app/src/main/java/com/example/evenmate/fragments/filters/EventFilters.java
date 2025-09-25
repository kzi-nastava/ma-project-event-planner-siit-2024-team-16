package com.example.evenmate.fragments.filters;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventFilters extends Filters {
    private Integer minMaxGuests;
    private Integer maxMaxGuests;
    private List<Long> selectedOrganizers;
    private String sortOption = "id,asc";


}
