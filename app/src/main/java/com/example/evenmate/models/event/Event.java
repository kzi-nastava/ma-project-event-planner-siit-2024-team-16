package com.example.evenmate.models.event;

import com.example.evenmate.models.Address;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.user.User;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event {
    private Long id;
    private String name;
    private String description;
    private Integer maxAttendees;
    private Boolean isPrivate;
    private Address address;
    private EventType type;
    private LocalDate date;
    private User organizer;
    private String photo;
}