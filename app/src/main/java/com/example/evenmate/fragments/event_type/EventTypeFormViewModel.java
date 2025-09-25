package com.example.evenmate.fragments.event_type;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.models.Category;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.event.EventTypeRequest;

import java.util.List;


public class EventTypeFormViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void resetState() {
        success.setValue(false);
        errorMessage.setValue(null);
    }
    public void fetchCategories() {
        Call<PaginatedResponse<Category>> call = ClientUtils.categoryService.getCategories();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Category>> call, @NonNull Response<PaginatedResponse<Category>> response) {
                Log.d("API_DEBUG", "Response code: " + response.code());
                Log.d("API_DEBUG", "Response body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue(response.body().getContent());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Category>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error fetching categories: " + t.getMessage());
            }
        });
    }
    public void addEventType(EventTypeRequest newEventType) {
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

    public void updateEventType(EventTypeRequest eventType) {
        Log.d("update", eventType.toString());

        Call<EventType> call = ClientUtils.eventTypeService.updateType(eventType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<EventType> call, @NonNull Response<EventType> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                } else {
                    errorMessage.postValue("Failed to update eventType. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventType> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
