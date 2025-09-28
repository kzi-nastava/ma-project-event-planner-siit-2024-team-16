package com.example.evenmate.models.service;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceFilter {
    @Nullable
    private String name;

    @Nullable
    private Double minPrice;

    @Nullable
    private Double maxPrice;

    @Nullable
    private Long categoryId;

    @Nullable
    private Long eventTypeId;

    @Nullable
    private Boolean isAvailable;

    public Map<String, String> toQueryMap() {
        Map<String, String> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value != null) {
                    map.put(field.getName(), String.valueOf(value));
                }
            } catch (IllegalAccessException e) {
                Log.e("ServiceFilter", "Error accessing field: " + field.getName(), e);
            }
        }
        return map;
    }
}
