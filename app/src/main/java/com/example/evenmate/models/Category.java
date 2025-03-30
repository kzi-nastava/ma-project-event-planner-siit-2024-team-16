package com.example.evenmate.models;

import androidx.annotation.NonNull;

import com.example.evenmate.models.event.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
