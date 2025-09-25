package com.example.evenmate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.pricelist.PriceListItem;
import com.example.evenmate.models.pricelist.PriceListItemUpdate;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceListViewModel extends ViewModel {
    private final MutableLiveData<List<PriceListItem>> priceList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(0);
    private int lastPageSize = 10;

    public LiveData<List<PriceListItem>> getPriceList() {
        return priceList;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getError() {
        return error;
    }
    public LiveData<Integer> getTotalPages() { return totalPages; }
    public LiveData<Integer> getCurrentPage() { return currentPage; }

    public void fetchPriceList(String type, Integer page, Integer size) {
        isLoading.setValue(true);
        if (size != null) lastPageSize = size;
        ClientUtils.priceListService.getPriceList(type, page, size).enqueue(new Callback<PaginatedResponse<PriceListItem>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<PriceListItem>> call, Response<PaginatedResponse<PriceListItem>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    PaginatedResponse<PriceListItem> resp = response.body();
                    priceList.setValue(resp.getContent());
                    totalPages.setValue(resp.getTotalPages());
                    currentPage.setValue(page != null ? page : 0);
                } else {
                    error.setValue("Failed to load price list");
                }
            }
            @Override
            public void onFailure(Call<PaginatedResponse<PriceListItem>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void updatePriceListItem(long assetId, double price, double discount, String selectedType) {
        isLoading.setValue(true);
        PriceListItemUpdate update = new PriceListItemUpdate(assetId, price, discount);
        ClientUtils.priceListService.updatePriceListItem(assetId, update).enqueue(new Callback<PriceListItem>() {
            @Override
            public void onResponse(Call<PriceListItem> call, Response<PriceListItem> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    fetchPriceList(selectedType, currentPage.getValue(), lastPageSize);
                } else {
                    error.setValue("Failed to update price list item");
                }
            }
            @Override
            public void onFailure(Call<PriceListItem> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}
