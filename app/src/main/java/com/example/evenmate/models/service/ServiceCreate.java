package com.example.evenmate.models.service;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCreate implements Serializable {
    private String name;
    private String description;
    private Double price;
    private Integer discount;
    private List<String> images;
    private Boolean isVisible;
    private Boolean isAvailable;

    @Nullable
    private Long categoryId;

    @Nullable
    private String newCategoryName;

    @Nullable
    private String newCategoryDescription;

    private String distinctiveness;

    @Nullable
    private Integer length;

    @Nullable
    private Integer minLength;

    @Nullable
    private Integer maxLength;

    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private String reservationConfirmation; // "Automatic" or "Manual"
}

