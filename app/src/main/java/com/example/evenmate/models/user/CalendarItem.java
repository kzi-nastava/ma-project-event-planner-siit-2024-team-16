package com.example.evenmate.models.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarItem {
    private Long id;
    private String name;
    private String dateTime;
    private Boolean isSpecial;
    private String type;
    private String color;
}
