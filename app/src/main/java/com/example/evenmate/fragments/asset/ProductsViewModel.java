package com.example.evenmate.fragments.asset;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.fragments.filters.AssetFilters;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.asset.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 5;
    @Setter
    private String fetchMode = "ALL_PRODUCTS";
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteFailed = new MutableLiveData<>(false);
    public LiveData<List<Product>> getProducts() { return products; }
    public Boolean getDeleteFailed() { return deleteFailed.getValue(); }

    public void resetDeleteFailed() { deleteFailed.setValue(false); }

    public LiveData<Integer> getCurrentPage() { return currentPage; }
    public LiveData<Integer> getTotalPages() { return totalPages; }

    public ProductsViewModel(){
    }

    public void fetchProducts() {
        int apiPage = currentPage.getValue() != null ? currentPage.getValue() - 1 : 0;
        Call<PaginatedResponse<Product>> call;
        if ("FAVORITES".equals(fetchMode)) {
            call = ClientUtils.userService.getFavoriteProducts(AuthManager.loggedInUser.getId(), apiPage, PAGE_SIZE);
        } else if ("YOUR_PRODUCTS".equals(fetchMode)) {
            call = ClientUtils.productService.getProductsByProvider(apiPage, PAGE_SIZE);
        } else {
            call = ClientUtils.productService.getProducts(apiPage, PAGE_SIZE);
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Product>> call, @NonNull Response<PaginatedResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body().getContent());
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Product>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error fetching products: " + t.getMessage());
            }
        });
    }

    public void deleteProduct(Long id) {
        Call<Object> call = ClientUtils.productService.delete(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                Log.d("delete", "deleted");
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error deleting product: " + t.getMessage());
                deleteFailed.setValue(true);
            }
        });
        refreshProducts();
    }

    public void refreshProducts() {
        List<Product> currentList = products.getValue();
        if (currentList != null) {
            products.setValue(new ArrayList<>(currentList));
        }
    }

    public void nextPage() {
        Integer current = currentPage.getValue();
        Integer total = totalPages.getValue();
        if (current != null && total != null && current < total) {
            currentPage.setValue(current + 1);
            fetchProducts();
        }
    }

    public void previousPage() {
        Integer current = currentPage.getValue();
        if (current != null && current > 1) {
            currentPage.setValue(current - 1);
            fetchProducts();
        }
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void applyFilters(@Nullable AssetFilters filters) {
        String type = "PRODUCT";
        Call<PaginatedResponse<Asset>> call = ClientUtils.assetService.getAll(
                filters != null ? filters.getSelectedLocations() : null,
                filters != null ? filters.getSelectedCategories() : null,
                filters != null ? filters.getSelectedTypes() : null,
                filters != null ? filters.getSelectedProviders() : null,
                filters != null ? filters.getDateFrom() : null,
                filters != null ? filters.getDateTo() : null,
                filters != null ? filters.getMinPrice() : null,
                filters != null ? filters.getMaxPrice() : null,
                filters != null ? filters.getMinRating() : null,
                filters != null ? filters.getMaxRating() : null,
                type,
                currentPage.getValue() - 1,
                PAGE_SIZE,
                filters != null ? filters.getSortOption() : null
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Asset>> call, @NonNull Response<PaginatedResponse<Asset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body().getContent().stream().map(Asset::toProduct).collect(Collectors.toList()));
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Asset>> call, @NonNull Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }
    public void searchProducts(String keywords) {
        Call<PaginatedResponse<Asset>> call = ClientUtils.assetService.search(keywords, currentPage.getValue()-1, PAGE_SIZE);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Asset>> call, @NonNull Response<PaginatedResponse<Asset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body().getContent().stream().map(Asset::toProduct).collect(Collectors.toList()));
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Asset>> call, @NonNull Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}