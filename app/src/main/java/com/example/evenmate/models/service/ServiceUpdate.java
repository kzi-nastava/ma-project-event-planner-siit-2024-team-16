package com.example.evenmate.models.service;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceUpdate implements Serializable {
    private String name;
    private String description;
    private Double price;
    private Integer discount;
    private List<String> images;
    private boolean isVisible;
    private boolean isAvailable;
    private String distinctiveness;

    @Nullable
    private Double length;

    @Nullable
    private Double minLength;

    @Nullable
    private Double maxLength;

    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private String reservationConfirmation; // "Automatic" or "Manual"
}

