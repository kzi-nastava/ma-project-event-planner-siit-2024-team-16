package com.example.evenmate.fragments.event_type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.models.EventType;
import com.example.evenmate.clients.ClientUtils;


public class CreateEventTypeViewModel extends ViewModel {
    private final MutableLiveData<EventType> eventTypeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<EventType> getEventType() {
        return eventTypeLiveData;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setEventType(EventType eventType) {
        eventTypeLiveData.setValue(eventType);
    }

    public void addEventType(EventType newEventType) {
        retrofit2.Call<EventType> call = ClientUtils.eventTypeService.createType(newEventType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<EventType> call, @NonNull Response<EventType> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to add eventType. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventType> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editEventType(EventType editEventType) {
        Call<EventType> call = ClientUtils.eventTypeService.updateType(editEventType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<EventType> call, @NonNull Response<EventType> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to edit eventType. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventType> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
