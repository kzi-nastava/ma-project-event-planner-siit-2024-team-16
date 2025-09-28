package com.example.evenmate.models.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends Asset{
    private String distinctiveness;
    private Integer length;
    private Integer minLength;
    private Integer maxLength;
    private Integer reservationDeadline;
    private Integer cancellationDeadline;
    private String reservationConformation;
}
