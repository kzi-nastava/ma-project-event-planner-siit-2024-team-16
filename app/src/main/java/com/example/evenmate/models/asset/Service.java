package com.example.evenmate.models.asset;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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
}