package com.example.evenmate.models;

public class Service {
    private String id;
    private String name;
    private String category;
    private String type;
    private double price;
    private boolean visible;
    private boolean available;
    private int imageResourceId;

    public Service(String id, String name, String category, String type,
                   double price, boolean available, boolean visible, int imageResourceId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.price = price;
        this.visible = visible;
        this.available = available;
        this.imageResourceId = imageResourceId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public int getImageResourceId() { return imageResourceId; }
}