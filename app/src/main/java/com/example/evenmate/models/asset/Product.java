package com.example.evenmate.models.asset;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product extends Asset{
    public Product(Long id, String title, ArrayList<String> image, String description, Integer price, String category, Integer discount, String country, String city, String street, String streetNumber, @Nullable Double rating, AssetType type, @Nullable Boolean isFavorite) {
        super(id, title, image, description, price, category, discount, country, city, street, streetNumber, rating, type, isFavorite);
    }
}
