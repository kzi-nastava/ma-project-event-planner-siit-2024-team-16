package com.example.evenmate.models.event;

import com.example.evenmate.models.Category;

import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventType {
    private Long id;
    private String name;
    private String description;
    private List<Category> recommendedCategories;
    private boolean isActive;

    public EventType() {}
    public EventType(Long id, String name, String description,
                     List<Category> recommendedCategories, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedCategories = recommendedCategories;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType eventType = (EventType) o;
        return isActive == eventType.isActive &&
                Objects.equals(id, eventType.id) &&
                Objects.equals(name, eventType.name) &&
                Objects.equals(description, eventType.description) &&
                Objects.equals(recommendedCategories, eventType.recommendedCategories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, recommendedCategories, isActive);
    }
}