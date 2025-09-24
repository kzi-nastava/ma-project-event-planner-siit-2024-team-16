package com.example.evenmate.fragments.event;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.event.EventRequest;
import com.example.evenmate.models.event.EventType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFormViewModel extends ViewModel {
    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> types = new MutableLiveData<>();


    public LiveData<Event> getEvent() {
        return eventLiveData;
    }

    public LiveData<List<EventType>> getTypes() {
        return types;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setEvent(Event event) {
        eventLiveData.setValue(event);
    }

    public void resetState() {
        success.setValue(null);
        errorMessage.setValue(null);
    }
    public void fetchTypes() {
        Call<PaginatedResponse<EventType>> call = ClientUtils.eventTypeService.getActiveTypes(true);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<EventType>> call, @NonNull Response<PaginatedResponse<EventType>> response) {
                Log.d("API_DEBUG", "Response code: " + response.code());
                Log.d("API_DEBUG", "Response body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    types.setValue(response.body().getContent());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<EventType>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error fetching types: " + t.getMessage());
            }
        });
    }
    public void addEvent(EventRequest newEvent) {
        retrofit2.Call<Event> call = ClientUtils.eventService.create(newEvent);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    success.postValue("Event successfully created.");
                } else {
                    errorMessage.postValue("Failed to add event. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void updateEvent(EventRequest event) {
        Call<Event> call = ClientUtils.eventService.update(event);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    success.postValue("Event successfully updated.");
                } else {
                    errorMessage.postValue("Failed to update event. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

}
