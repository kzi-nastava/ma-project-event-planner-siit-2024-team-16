package com.example.evenmate.models.asset;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    private Long eventId;
    private Long serviceId;
    private Integer desirableLength;
    private LocalDateTime desirableStart;
}
