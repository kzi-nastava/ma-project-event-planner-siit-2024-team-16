package com.example.evenmate.fragments.event_type;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypesViewModel extends ViewModel {
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<List<EventType>> getEventTypes() { return eventTypes; }

    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public LiveData<Integer> getTotalPages() { return totalPages; }

    public EventTypesViewModel(){
        fetchEventTypes();
    }


    public void fetchEventTypes() {
        Call<ArrayList<EventType>> call = ClientUtils.eventTypeService.getTypes(1,5);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<EventType>> call, @NonNull Response<ArrayList<EventType>> response) {
                if (response.isSuccessful()) {
                    eventTypes.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch eventTypes. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<EventType>> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
        totalPages.setValue(Objects.requireNonNull(eventTypes.getValue()).size());
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
        int current = currentPage.getValue() != null ? currentPage.getValue() : 1;
        int total = totalPages.getValue() != null ? totalPages.getValue() : 1;
        currentPage.setValue(Math.min(current + 1, total));
    }

    public void previousPage() {
        int current = currentPage.getValue() != null ? currentPage.getValue() : 1;
        currentPage.setValue(Math.max(current - 1, 1));
    }
}