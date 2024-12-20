package com.example.evenmate.models;

import java.util.List;
import java.util.Objects;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Category> getRecommendedCategories() { return recommendedCategories; }
    public void setRecommendedCategories(List<Category> recommendedCategories) {
        this.recommendedCategories = recommendedCategories;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

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
