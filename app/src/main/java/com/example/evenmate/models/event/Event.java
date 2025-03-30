package com.example.evenmate.models.event;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private Long id;
    private String type;
    private String name;
    private String description;
    private Integer maxAttendants;
    private boolean isPrivate;
    private LocalDateTime date;
    private ArrayList<String> invited;
    private String country;
    private String city;
    private String street;
    private String streetNumber;
    private String image;
    @Nullable
    private Double rating;
    @Nullable
    private Boolean isFavorite;
    // constructor for now
    public Event(Long id, String type, String name, String description, Integer maxAttendants, boolean isPrivate,
                 LocalDateTime date, ArrayList<String> invited, String country, String city, String street,
                 String streetNumber, String image, @Nullable Double rating, @Nullable Boolean isFavorite) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.maxAttendants = maxAttendants;
        this.isPrivate = isPrivate;
        this.date = date;
        this.invited = invited;
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.image = image;
        this.rating = rating;
        this.isFavorite=isFavorite;

    }
}