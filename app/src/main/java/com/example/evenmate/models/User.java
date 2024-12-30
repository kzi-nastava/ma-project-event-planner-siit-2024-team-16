package com.example.evenmate.models;

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Address address;
    private String phone;
    private Company company;
    private String photo;

    public User() {
        this.address = new Address();
    }

    public User(String email, String password, String firstName, Address address, String lastName, String phone, Company company, String photo) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.address = address;
        this.lastName = lastName;
        this.phone = phone;
        this.company = company;
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
