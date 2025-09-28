package com.example.evenmate.fragments.product;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.asset.Product;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewModel extends ViewModel {
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
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

    public void toggleFavorite() {
        // TODO: 
        isFavorite.setValue(!Boolean.TRUE.equals(isFavorite.getValue()));
    }

    public void purchaseProduct() {
        // TODO:
    }

    public void initiateChat() {
        // TODO:
    }
}
