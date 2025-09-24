package com.example.evenmate.models.asset;

import com.example.evenmate.models.category.Category;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer discount;
    private List<String> images;
    private Boolean isVisible;
    private Boolean isAvailable;
//    TODO: private ProductServiceProvider provider;
    private Category category;
    private Double averageReview;
    private AssetType type;
    private Boolean isVisibleToUser;

    public String getNewPrice(){
        Double newPrice = Double.valueOf(this.price);
        newPrice-=newPrice*this.discount/100;
        return newPrice.toString();
    }
}