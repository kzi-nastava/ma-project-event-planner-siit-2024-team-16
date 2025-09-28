package com.example.evenmate.fragments.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.service.ServiceCreate;
import com.example.evenmate.models.service.ServiceUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFormViewModel extends ViewModel {
    private final MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public LiveData<Service> getService() {
        return serviceLiveData;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setService(Service service) {
        serviceLiveData.setValue(service);
    }

    public void fetchCategories() {
        ClientUtils.categoryService.getCategories(null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Category>> call, @NonNull Response<PaginatedResponse<Category>> response) {
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

    public void addService(ServiceCreate service) {
        ClientUtils.serviceService.add(service).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue("Service successfully created.");
                } else {
                    errorMessage.postValue("Failed to add service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
    public void updateService(Long serviceId, ServiceUpdate service) {
        Call<Service> call = ClientUtils.serviceService.update(serviceId, service);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue("Service successfully updated.");
                } else {
                    errorMessage.postValue("Failed to update service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}

