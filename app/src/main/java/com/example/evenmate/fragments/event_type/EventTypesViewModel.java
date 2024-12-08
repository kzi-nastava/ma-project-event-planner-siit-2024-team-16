package com.example.evenmate.fragments.event_type;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.R;
import com.example.evenmate.models.EventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventTypesViewModel extends ViewModel {
    private MutableLiveData<List<EventType>> eventTypes;
    private MutableLiveData<Integer> currentPage;
    private MutableLiveData<Integer> totalPages;

    public EventTypesViewModel() {
        eventTypes = new MutableLiveData<>();
        currentPage = new MutableLiveData<>(1);
        totalPages = new MutableLiveData<>(1);
        fetchEventTypes();
    }

    public LiveData<List<EventType>> getEventTypes() {
        return eventTypes;
    }

    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

    public LiveData<Integer> getTotalPages() {
        return totalPages;
    }

    private void fetchEventTypes() {
        // Mock data - replace with actual data fetching logic
        List<EventType> mockEventTypes = Arrays.asList(
                new EventType("1", "Conference", "Large gathering for professionals",
                        Arrays.asList("Corporate", "Academic"), true),
                new EventType("2", "Workshop", "Hands-on learning session",
                        Arrays.asList("Educational", "Training"), false),
                new EventType("3", "Seminar", "Lecture-style event",
                        Arrays.asList("Professional Development"), true)
        );
        eventTypes.setValue(mockEventTypes);
        totalPages.setValue(1);
    }

    public void toggleEventTypeStatus(String eventTypeId) {
        List<EventType> currentEventTypes = eventTypes.getValue();
        if (currentEventTypes == null) {
            return;
        }
        // Go though the list until it finds event type with the given  id
        for (int i = 0; i < currentEventTypes.size(); i++) {
            if (currentEventTypes.get(i).getId().equals(eventTypeId)) {
                List<EventType> updatedEventTypes = new ArrayList<>(currentEventTypes);
                updatedEventTypes.get(i).setActive(!updatedEventTypes.get(i).isActive());
                eventTypes.setValue(updatedEventTypes);
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

    public int getButtonStyle(String text) {
        if ("ACTIVATE".equals(text)) {
            return R.style.GreenOutlinedButton;
        } else if ("DEACTIVATE".equals(text)) {
            return R.style.RedOutlinedButton;
        }
        return R.style.GreenOutlinedButton;
    }
}
