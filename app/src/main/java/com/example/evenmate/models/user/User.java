package com.example.evenmate.models.user;

import com.example.evenmate.models.Address;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private Company company;
    private String photo;
    private String role;

    public User() {
        this.address = new Address();
    }

    public User(String email, String password, String firstName, Address address, String lastName, String phone, Company company, String photo, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.address = address;
        this.lastName = lastName;
        this.phone = phone;
        this.company = company;
        this.photo = photo;
        this.role = role;
    }

}
