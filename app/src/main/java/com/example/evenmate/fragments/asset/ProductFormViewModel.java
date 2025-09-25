package com.example.evenmate.fragments.asset;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.asset.ProductRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFormViewModel extends ViewModel {
    private final MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public LiveData<Product> getProduct() {
        return productLiveData;
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

    public void setProduct(Product product) {
        productLiveData.setValue(product);
    }

    public void fetchCategories() {
        Call<PaginatedResponse<Category>> call = ClientUtils.categoryService.getAllForFilters();
        call.enqueue(new Callback<>() {
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

    public void addProduct(ProductRequest newProduct) {
        Call<Product> call = ClientUtils.productService.create(newProduct);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful()) {
                    success.postValue("Product successfully created.");
                } else {
                    errorMessage.postValue("Failed to add product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
    public void updateProduct(ProductRequest newProduct) {
        Call<Product> call = ClientUtils.productService.update(newProduct, newProduct.getId());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful()) {
                    success.postValue("Product successfully updated.");
                } else {
                    errorMessage.postValue("Failed to update product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}

