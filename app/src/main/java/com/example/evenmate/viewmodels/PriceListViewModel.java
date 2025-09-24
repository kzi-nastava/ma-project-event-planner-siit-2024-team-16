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

    public LiveData<List<PriceListItem>> getPriceList() {
        return priceList;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getError() {
        return error;
    }

    public void fetchPriceList(String type, Integer page, Integer size) {
        isLoading.setValue(true);
        ClientUtils.priceListService.getPriceList(type, page, size).enqueue(new Callback<PaginatedResponse<PriceListItem>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<PriceListItem>> call, Response<PaginatedResponse<PriceListItem>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    priceList.setValue(response.body().getContent());
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

    public void updatePriceListItem(long assetId, double price, double discount) {
        isLoading.setValue(true);
        PriceListItemUpdate update = new PriceListItemUpdate(assetId, price, discount);
        ClientUtils.priceListService.updatePriceListItem(assetId, update).enqueue(new Callback<PriceListItem>() {
            @Override
            public void onResponse(Call<PriceListItem> call, Response<PriceListItem> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    fetchPriceList(null, null, null); // TODO: type
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

