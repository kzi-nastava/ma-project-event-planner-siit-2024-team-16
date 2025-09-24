package com.example.evenmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.category.CategoryRequest;
import com.example.evenmate.models.category.CategorySuggestion;
import com.example.evenmate.models.PaginatedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySuggestion>> suggestions = new MutableLiveData<>();

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<CategorySuggestion>> getSuggestions() {
        return suggestions;
    }

    public void fetchCategories() {
        isLoading.setValue(true);
        ClientUtils.categoryService.getCategories(null, null).enqueue(new Callback<PaginatedResponse<Category>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<Category>> call, Response<PaginatedResponse<Category>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    categories.setValue(response.body().getContent());
                } else {
                    error.setValue("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<Category>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void createCategory(CategoryRequest request) {
        isLoading.setValue(true);
        ClientUtils.categoryService.createCategory(request).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    fetchCategories();
                } else {
                    error.setValue("Failed to create category");
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void updateCategory(Long id, CategoryRequest request) {
        isLoading.setValue(true);
        ClientUtils.categoryService.updateCategory(id, request).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    fetchCategories();
                } else {
                    error.setValue("Failed to update category");
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void deleteCategory(Long id) {
        isLoading.setValue(true);
        ClientUtils.categoryService.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    fetchCategories();
                } else {
                    error.setValue("Failed to delete category");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void fetchSuggestions() {
        isLoading.setValue(true);
        ClientUtils.categoryService.getCategorySuggestions(null, null).enqueue(new Callback<PaginatedResponse<CategorySuggestion>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<CategorySuggestion>> call, Response<PaginatedResponse<CategorySuggestion>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    suggestions.setValue(response.body().getContent());
                } else {
                    error.setValue("Failed to load suggestions");
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<CategorySuggestion>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}

