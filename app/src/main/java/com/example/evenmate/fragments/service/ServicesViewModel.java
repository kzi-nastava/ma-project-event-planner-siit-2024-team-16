package com.example.evenmate.fragments.service;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.service.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesViewModel extends ViewModel {
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Service>> getServices() {
        return services;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchServices() {
        ClientUtils.serviceService.getAll(null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Service>> call, Response<PaginatedResponse<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    services.postValue(response.body().getContent());
                } else {
                    errorMessage.postValue("Failed to fetch services.");
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Service>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching services: " + t.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteService(long serviceId) {
        Call<Void> call = ClientUtils.serviceService.delete(serviceId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.postValue("Failed to delete service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}

