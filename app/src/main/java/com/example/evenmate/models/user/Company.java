package com.example.evenmate.models.user;

import com.example.evenmate.models.Address;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Company {
    private String email;
    private String name;
    private Address address;
    private String phone;
    private String description;
    private List<String> photos;
}
