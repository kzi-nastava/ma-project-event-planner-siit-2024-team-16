package com.example.evenmate.models.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarDay {
    private LocalDateTime dateTime;
    private boolean isCurrentMonth;
    private boolean isToday;
    private List<CalendarItem> events;

    public CalendarDay(LocalDateTime dateTime, boolean isCurrentMonth) {
        this.dateTime = dateTime;
        this.isCurrentMonth = isCurrentMonth;
        this.isToday = dateTime != null && dateTime.equals(LocalDateTime.now());
        this.events = new ArrayList<>();
    }

    public void addEvent(CalendarItem event) { this.events.add(event); }
}
