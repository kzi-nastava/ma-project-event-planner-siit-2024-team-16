package com.example.evenmate.fragments.event_type;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.EventType;
import com.example.evenmate.models.PaginatedResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypesViewModel extends ViewModel {
    private static final int PAGE_SIZE = 5;
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<List<EventType>> getEventTypes() { return eventTypes; }

    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public LiveData<Integer> getTotalPages() { return totalPages; }

    public EventTypesViewModel(){
//        fetchEventTypes();
    }


    public void fetchEventTypes() {
        int apiPage = currentPage.getValue() != null ? currentPage.getValue() - 1 : 0;
        Call<PaginatedResponse<EventType>> call = ClientUtils.eventTypeService.getTypes(apiPage, PAGE_SIZE);
        call.enqueue(new Callback<PaginatedResponse<EventType>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<EventType>> call, Response<PaginatedResponse<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue(response.body().getContent());
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<EventType>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching event types: " + t.getMessage());
            }
        });
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
        //TODO: Update event type
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
        Integer current = currentPage.getValue();
        Integer total = totalPages.getValue();
        if (current != null && total != null && current < total) {
            currentPage.setValue(current + 1);
            fetchEventTypes();
        }
    }

    public void previousPage() {
        Integer current = currentPage.getValue();
        if (current != null && current > 1) {
            currentPage.setValue(current - 1);
            fetchEventTypes();
        }
    }
}