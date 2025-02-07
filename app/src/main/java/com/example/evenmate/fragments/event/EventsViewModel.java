package com.example.evenmate.fragments.event;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.Event;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 5;
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<List<Event>> getEvents() { return events; }

    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public LiveData<Integer> getTotalPages() { return totalPages; }

    public EventsViewModel(){
    }

    public void fetchEvents() {
        int apiPage = currentPage.getValue() != null ? currentPage.getValue() - 1 : 0;
        Call<PaginatedResponse<Event>> call = ClientUtils.eventService.getEvents(apiPage, PAGE_SIZE);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Event>> call, @NonNull Response<PaginatedResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.setValue(response.body().getContent());
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Event>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error fetching events: " + t.getMessage());
            }
        });
    }

    public void refreshEvents() {
        List<Event> currentList = events.getValue();
        if (currentList != null) {
            events.setValue(new ArrayList<>(currentList));
        }
    }

    public void nextPage() {
        Integer current = currentPage.getValue();
        Integer total = totalPages.getValue();
        if (current != null && total != null && current < total) {
            currentPage.setValue(current + 1);
            fetchEvents();
        }
    }

    public void previousPage() {
        Integer current = currentPage.getValue();
        if (current != null && current > 1) {
            currentPage.setValue(current - 1);
            fetchEvents();
        }
    }

    public void updateFavoriteStatus(Event event) {
        //TODO
//        boolean newStatus = !event.isActive();
//        event.setActive(newStatus);
//
//        Call<Event> call = ClientUtils.eventService.update(event);
//        call.enqueue(new Callback<>() {
//            @Override
//            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Event> currentList = events.getValue();
//                    if (currentList != null) {
//                        int index = currentList.indexOf(event);
//                        if (index != -1) {
//                            currentList.set(index, response.body());
//                            events.setValue(currentList);
//                        }
//                    }
//                } else {
//                    event.setActive(!newStatus);
//                    errorMessage.setValue("Failed to update event status");
//                    refreshEvents();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
//                event.setActive(!newStatus);
//                errorMessage.setValue("Network error while updating status");
//                refreshEvents();
//            }
//        });
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}