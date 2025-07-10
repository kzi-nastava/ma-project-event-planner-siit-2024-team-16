package com.example.evenmate.models.asset;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Asset {
    private Long id;
    private String title;
    private ArrayList<String> image;
    private String description;
    private Integer price;
    private String category;
    private Integer discount;
    private String country;
    private String city;
    private String street;
    private String streetNumber;
    @Nullable
    private Double rating;
    private AssetType type;
    @Nullable
    private Boolean isFavorite;
    public Asset(Long id, String title, ArrayList<String> image, String description, Integer price,
                 String category, Integer discount, String country, String city, String street,
                 String streetNumber, @Nullable Double rating, AssetType type,@Nullable Boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.price = price;
        this.category = category;
        this.discount = discount;
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.rating = rating;
        this.type = type;
        this.isFavorite=isFavorite;
    }
    public String getNewPrice(){
        Double newPrice = Double.valueOf(this.price);
        newPrice-=newPrice*this.discount/100;
        return newPrice.toString();
    }
}