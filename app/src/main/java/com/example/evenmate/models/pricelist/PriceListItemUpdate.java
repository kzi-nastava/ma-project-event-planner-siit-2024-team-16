package com.example.evenmate.models.pricelist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceListItemUpdate {
    private Long assetId;
    private Double price;
    private Double discount;
}

