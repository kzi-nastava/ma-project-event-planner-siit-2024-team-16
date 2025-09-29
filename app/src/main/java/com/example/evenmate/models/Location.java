package com.example.evenmate.models;
import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    @Nullable
    private String city;
    private String country;
}
