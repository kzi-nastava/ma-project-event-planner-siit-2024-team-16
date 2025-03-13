package com.example.evenmate.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Address {
    private String city;
    private String streetName;
    private String streetNumber;

    public Address() {
    }

    public Address(String streetName, String streetNumber, String city) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
    }

}
