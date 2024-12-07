package com.example.evenmate.models;

import java.util.List;
import java.util.Objects;

public class EventType {
    private String id;
    private String name;
    private String description;
    private List<String> recommendedCategories;
    private boolean isActive;

    public EventType(String id, String name, String description,
                     List<String> recommendedCategories, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedCategories = recommendedCategories;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRecommendedCategories() { return recommendedCategories; }
    public void setRecommendedCategories(List<String> recommendedCategories) {
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
