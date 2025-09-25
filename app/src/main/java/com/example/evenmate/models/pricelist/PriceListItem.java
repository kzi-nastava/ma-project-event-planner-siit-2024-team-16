package com.example.evenmate.models.pricelist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceListItem {
    private Long id; // Asset ID
    private String name;
    private Double price;
    private Double discount;
    private Double priceAfterDiscount;
}

