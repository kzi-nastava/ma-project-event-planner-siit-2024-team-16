package com.example.evenmate.fragments.event_type;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.models.EventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventTypesViewModel extends ViewModel {
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);

    public LiveData<List<EventType>> getEventTypes() { return eventTypes; }

    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public LiveData<Integer> getTotalPages() { return totalPages; }

    public EventTypesViewModel(){
        fetchEventTypes();
    }


    private void fetchEventTypes() {
        // Initial mock data
        List<EventType> mockEventTypes = new ArrayList<>(Arrays.asList(
                new EventType("1", "Conference", "Large gathering for professionals",
                        Arrays.asList("Corporate", "Academic"), true),
                new EventType("2", "Workshop", "Hands-on learning session",
                        Arrays.asList("Educational", "Training"), false),
                new EventType("3", "Seminar", "Lecture-style event",
                        List.of("Professional Development"), true)
        ));
        eventTypes.setValue(mockEventTypes);
        totalPages.setValue(1);
    }

    public void addEventType(EventType newEventType) {
        Log.d("EventTypesViewModel", "addEventType called");
        List<EventType> currentList = eventTypes.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        if (newEventType.getId() == null || newEventType.getId().isEmpty()) {
            newEventType.setId(String.valueOf(System.currentTimeMillis()));
        }

        currentList.add(0, newEventType);

        List<EventType> updatedList = new ArrayList<>(currentList);
        updatedList.add(0, newEventType);

        // Explicitly post the new list
        eventTypes.postValue(updatedList);
    }
    public void refreshEventTypes() {
        List<EventType> currentList = eventTypes.getValue();
        if (currentList != null) {
            eventTypes.setValue(new ArrayList<>(currentList));
        }
    }

    public void toggleEventTypeStatus(String eventTypeId) {
        List<EventType> currentEventTypes = eventTypes.getValue();
        if (currentEventTypes == null) {
            return;
        }

        for (int i = 0; i < currentEventTypes.size(); i++) {
            if (currentEventTypes.get(i).getId().equals(eventTypeId)) {
                EventType eventType = currentEventTypes.get(i);
                eventType.setActive(!eventType.isActive());
                eventTypes.setValue(currentEventTypes);
                break;
            }
        }
    }

    public void nextPage() {
        int current = currentPage.getValue() != null ? currentPage.getValue() : 1;
        int total = totalPages.getValue() != null ? totalPages.getValue() : 1;
        currentPage.setValue(Math.min(current + 1, total));
    }

    public void previousPage() {
        int current = currentPage.getValue() != null ? currentPage.getValue() : 1;
        currentPage.setValue(Math.max(current - 1, 1));
    }
}