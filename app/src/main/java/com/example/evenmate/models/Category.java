package com.example.evenmate.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Category {
    private Long id;
    private String name;
    private String description;
    private List<EventType> types;

    public Category(Long id, String name, String description, List<EventType> types) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.types = types;
    }

    public Category(Long id, String name, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.types = new ArrayList<>();
    }

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EventType> getTypes() {
        return types;
    }

    public void setTypes(List<EventType> types) {
        this.types = types;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) && Objects.equals(description, category.description) && Objects.equals(types, category.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, types);
    }
}
