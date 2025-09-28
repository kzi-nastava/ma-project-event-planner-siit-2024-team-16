package com.example.evenmate.models.service;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreate implements Serializable {
    private String name;
    private String description;
    private Double price;
    private Integer discount;

    @Nullable
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

