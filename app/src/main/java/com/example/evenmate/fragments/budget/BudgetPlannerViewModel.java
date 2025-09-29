package com.example.evenmate.fragments.budget;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.budget.BudgetAsset;
import com.example.evenmate.models.budget.BudgetItem;
import com.example.evenmate.models.budget.BudgetItemCreate;
import com.example.evenmate.models.budget.BudgetItemUpdate;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetPlannerViewModel extends ViewModel {
    @Setter
    private Long eventId;
    private final MutableLiveData<List<BudgetItem>> budgetItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalBudget = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<BudgetAsset>> budgetAssets = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> recommendedCategories = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<BudgetItem>> getBudgetItems() { return budgetItems; }
    public LiveData<Double> getTotalBudget() { return totalBudget; }
    public LiveData<List<BudgetAsset>> getBudgetAssets() { return budgetAssets; }
    public LiveData<List<Category>> getRecommendedCategories() { return recommendedCategories; }

    public void fetchBudgetItems() {
        ClientUtils.budgetService.getAll(eventId, null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<BudgetItem>> call, Response<PaginatedResponse<BudgetItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BudgetItem> items = response.body().getContent();
                    budgetItems.setValue(items);
                    updateTotalBudget();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<BudgetItem>> call, Throwable throwable) {
                Log.e("API_ERROR", "Error fetching budget items: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error fetching budget items: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchBudgetAssets(Long budgetItemId) {
        ClientUtils.budgetService.getAssetsByBudgetItemId(budgetItemId, null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PaginatedResponse<BudgetAsset>> call, Response<PaginatedResponse<BudgetAsset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    budgetAssets.setValue(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<BudgetAsset>> call, Throwable throwable) {
                Log.e("API_ERROR", "Error fetching budget assets: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error fetching budget assets: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchRecommendedCategories(Long eventId) {
        ClientUtils.eventService.getRecommendedCategories(eventId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    recommendedCategories.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                Log.e("API_ERROR", "Error fetching recommended categories: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error fetching recommended categories: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBudgetItem(BudgetItemCreate budgetItem) {
        ClientUtils.budgetService.add(budgetItem).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems();
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<BudgetItem> call, Throwable throwable) {
                Log.e("API_ERROR", "Error adding budget item: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error adding budget item: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void editBudgetItem(Long budgetItemId, Double newAmount) {
        ClientUtils.budgetService.update(budgetItemId, new BudgetItemUpdate(newAmount)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BudgetItem> call, Response<BudgetItem> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems();
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<BudgetItem> call, Throwable throwable) {
                Log.e("API_ERROR", "Error updating budget item: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error updating budget item: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteBudgetItem(Long budgetItemId) {
        ClientUtils.budgetService.delete(budgetItemId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchBudgetItems();
                } else {
                    ErrorUtils.showErrorToast(response, ClientUtils.getContext());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("API_ERROR", "Error deleting budget item: " + throwable.getMessage());
                Toast.makeText(ClientUtils.getContext(), "Error deleting budget item: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTotalBudget() {
        double total = 0.0;
        for (BudgetItem i : budgetItems.getValue()) total += i.getAmount();
        totalBudget.setValue(total);
    }
}

