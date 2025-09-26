package com.example.evenmate.models.asset;

import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.user.User;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product extends Asset{
    public Product(Long id, String name, String description, Double price, Integer discount,
                   Double priceAfterDiscount, User provider, List<String> images,
                   Category category, Double averageReview, AssetType type,
                   Boolean isVisible, Boolean isAvailable, Boolean isVisibleToUser) {
        super(id, name, description, price, discount, priceAfterDiscount, provider,
                images, category, averageReview, type, isVisible, isAvailable, isVisibleToUser);
    }
}
