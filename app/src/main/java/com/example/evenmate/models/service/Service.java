package com.example.evenmate.models.service;

import com.example.evenmate.models.asset.Asset;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Service extends Asset implements Serializable {
    private String distinctiveness;
    private Integer length;
    private Integer minLength;
    private Integer maxLength;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private String reservationConfirmation; // "Automatic" or "Manual"
}

