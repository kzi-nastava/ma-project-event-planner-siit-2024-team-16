package com.example.evenmate.models.user;

import com.example.evenmate.models.Address;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private String email;
    private String name;
    private Address address;
    private String phone;
    private String description;
    private List<String> photos;

    public Company() {
        this.address = new Address();
        this.photos = new ArrayList<>();
    }

    public Company(String email, String name, Address address, String description, String phone, List<String> photos) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.photos = photos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
