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
import com.example.evenmate.models.service.ServiceFilter;
import com.example.evenmate.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesViewModel extends ViewModel {
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalElements = new MutableLiveData<>();

    public LiveData<List<Service>> getServices() {
        return services;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<EventType>> getEventTypes() {
        return eventTypes;
    }

    public LiveData<Integer> getTotalPages() {
        return totalPages;
    }

    public LiveData<Integer> getTotalElements() {
        return totalElements;
    }

    public void fetchServices(int page, int size, ServiceFilter filters) {
        ClientUtils.serviceService.getAll(page, size, filters.toQueryMap()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Service>> call, Response<PaginatedResponse<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    services.postValue(response.body().getContent());
                    totalPages.postValue(response.body().getTotalPages());
                    totalElements.postValue(response.body().getTotalElements());
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

    public void fetchCategories() {
        ClientUtils.categoryService.getCategories(null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Category>> call, Response<PaginatedResponse<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.postValue(response.body().getContent());
                } else {
                    errorMessage.postValue("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Category>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching categories: " + t.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchEventTypes() {
        ClientUtils.serviceService.getEventTypes(null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<EventType>> call, Response<PaginatedResponse<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.postValue(response.body().getContent());
                } else {
                    errorMessage.postValue("Failed to fetch event types.");
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<EventType>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching event types: " + t.getMessage());
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
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}

