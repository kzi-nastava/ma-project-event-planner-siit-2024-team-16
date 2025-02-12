package com.example.evenmate.fragments.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.event.Event;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFormViewModel extends ViewModel {
    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Event> getEvent() {
        return eventLiveData;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setEvent(Event event) {
        eventLiveData.setValue(event);
    }

    public void addEvent(Event newEvent) {
        retrofit2.Call<Event> call = ClientUtils.eventService.create(newEvent);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
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

    public void updateEvent(Event event) {
        Call<Event> call = ClientUtils.eventService.update(event);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
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
