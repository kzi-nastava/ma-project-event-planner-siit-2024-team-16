package com.example.evenmate.models.asset;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Service extends Asset{
    private String distinctiveness;
    @Nullable
    private Integer length;
    @Nullable
    private Integer minLength;
    @Nullable
    private Integer maxLength;
    private String reservationDeadline;
    private String cancellationDeadline;
    private String reservationConformation;
    private ArrayList<String> eventTypes;


    public Service(Long id, String title, ArrayList<String> image, String description, Integer price, String category, Integer discount, String country, String city, String street, String streetNumber, @Nullable Double rating, AssetType type, @Nullable Boolean isFavorite, String distinctiveness,@Nullable Integer length, @Nullable Integer maxLength,@Nullable Integer minLength, String reservationDeadline, String reservationConformation, String cancellationDeadline, ArrayList<String> eventTypes) {
        super(id, title, image, description, price, category, discount, country, city, street, streetNumber, rating, type, isFavorite);
        this.distinctiveness=distinctiveness;
        this.length=length;
        this.maxLength=maxLength;
        this.minLength=minLength;
        this.reservationDeadline=reservationDeadline;
        this.reservationConformation=reservationConformation;
        this.cancellationDeadline=cancellationDeadline;
        this.eventTypes=eventTypes;
    }
}
