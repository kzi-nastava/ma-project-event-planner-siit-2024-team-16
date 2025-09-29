package com.example.evenmate.models.asset;

import com.example.evenmate.models.event.Event;

import java.time.LocalDateTime;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Reservation {
    private Long id;
    private Service service;
    private LocalDateTime dateTime;
    private Integer length;
    private Double price;
    private Event event;
}