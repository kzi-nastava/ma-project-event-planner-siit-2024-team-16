package com.example.evenmate.fragments.product;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.asset.PurchaseRequest;
import com.example.evenmate.models.chat.Chat;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.utils.ErrorUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewModel extends ViewModel {
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public void fetchProductById(Long productId) {
        ClientUtils.productService.getById(productId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ProductDetailsViewModel", "Failed to load product", t);
                Toast.makeText(ClientUtils.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchEvents() {
        ClientUtils.eventService.getEventsByOrganizer(null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Event>> call, Response<PaginatedResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.postValue(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Event>> call, Throwable throwable) {
                Log.e("ProductDetailsViewModel", "Failed to load events", throwable);
                Toast.makeText(ClientUtils.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void isFavorite(Long productId) {
        ClientUtils.productService.isFavorite(productId).enqueue(new Callback<>() {
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

    public void toggleFavorite(Long productId) {
        ClientUtils.productService.toggleFavorite(productId).enqueue(new Callback<>() {
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

    public void purchaseProduct(Long productId, Long eventId) {
        ClientUtils.productService.buyProduct(productId, new PurchaseRequest(eventId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClientUtils.getContext(), "Purchase successful!", Toast.LENGTH_SHORT).show();
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("ProductDetailsViewModel", "Failed to purchase product", throwable);
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
