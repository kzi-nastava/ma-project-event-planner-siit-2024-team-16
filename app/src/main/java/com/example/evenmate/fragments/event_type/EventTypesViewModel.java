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
    }


    public void fetchEventTypes() {
        int apiPage = currentPage.getValue() != null ? currentPage.getValue() - 1 : 0;
        Call<PaginatedResponse<EventType>> call = ClientUtils.eventTypeService.getTypes(apiPage, PAGE_SIZE);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<EventType>> call, @NonNull Response<PaginatedResponse<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.setValue(response.body().getContent());
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<EventType>> call, @NonNull Throwable t) {
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

    public void updateEventTypeStatus(EventType eventType) {
        boolean newStatus = !eventType.isActive();
        eventType.setActive(newStatus);

        Call<EventType> call = ClientUtils.eventTypeService.updateType(eventType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<EventType> call, @NonNull Response<EventType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update the item in the current list
                    List<EventType> currentList = eventTypes.getValue();
                    if (currentList != null) {
                        int index = currentList.indexOf(eventType);
                        if (index != -1) {
                            currentList.set(index, response.body());
                            eventTypes.setValue(currentList);
                        }
                    }
                } else {
                    // Revert the status if update failed
                    eventType.setActive(!newStatus);
                    errorMessage.setValue("Failed to update event type status");
                    refreshEventTypes();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventType> call, @NonNull Throwable t) {
                eventType.setActive(!newStatus);
                errorMessage.setValue("Network error while updating status");
                refreshEventTypes();
            }
        });
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}