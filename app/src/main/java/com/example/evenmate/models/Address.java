package com.example.evenmate.models;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}
