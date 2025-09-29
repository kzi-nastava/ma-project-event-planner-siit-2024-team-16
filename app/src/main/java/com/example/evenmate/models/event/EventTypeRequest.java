package com.example.evenmate.models.event;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventTypeRequest {
    private Long id;
    private String name;
    private String description;
    private List<Long> recommendedCategoryIds;
    private boolean isActive;

    public EventTypeRequest() {
    }

    public EventTypeRequest(Long id, String name, String description, List<Long> recommendedCategoryIds, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedCategoryIds = recommendedCategoryIds;
        this.isActive = isActive;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventTypeRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", recommendedCategoriesId=" + recommendedCategoryIds +
                ", isActive=" + isActive +
                '}';
    }
}
