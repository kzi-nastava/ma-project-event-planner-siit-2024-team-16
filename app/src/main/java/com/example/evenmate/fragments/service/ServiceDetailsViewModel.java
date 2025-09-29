package com.example.evenmate.fragments.service;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.evenmate.clients.ServiceService;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.chat.Chat;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsViewModel extends ViewModel {
    private final MutableLiveData<Service> service = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public LiveData<Service> getService() {
        return service;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void fetchServiceById(Long serviceId) {
        ServiceService serviceService = ClientUtils.serviceService;
        serviceService.getById((long) serviceId).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful() && response.body() != null) {
                    service.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                Log.e("ServiceDetailsViewModel", "Failed to load service", t);
                Toast.makeText(ClientUtils.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void isFavorite(Long serviceId) {
        ClientUtils.serviceService.isFavorite(serviceId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite.postValue(response.body());
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("ProductDetailsViewModel", "Failed to check favorite status", t);
                Toast.makeText(ClientUtils.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toggleFavorite(Long serviceId) {
        ClientUtils.serviceService.toggleFavorite(serviceId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isFavorite.setValue(!Boolean.TRUE.equals(isFavorite.getValue()));
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("ProductDetailsViewModel", "Failed to toggle favorite", throwable);
                Toast.makeText(ClientUtils.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initiateChat(Long providerId) {
        ClientUtils.chatService.initiateChat(providerId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Chat> call, Response<Chat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ClientUtils.getContext(), "Chat initiated, check your chats.", Toast.LENGTH_SHORT).show();
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable throwable) {
                Log.e("ProductDetailsViewModel", "Failed to initiate chat", throwable);
                Toast.makeText(ClientUtils.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
