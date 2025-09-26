package com.example.evenmate.models.user;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventOrganizer {
    private User user;
    private List<Long> eventIds;
}
