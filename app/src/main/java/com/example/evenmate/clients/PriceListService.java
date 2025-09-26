package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.pricelist.PriceListItem;
import com.example.evenmate.models.pricelist.PriceListItemUpdate;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PriceListService {
    @GET("assets/price-list")
    Call<PaginatedResponse<PriceListItem>> getPriceList(@Query("type") String type, @Query("page") Integer page, @Query("size") Integer size);

    @PUT("assets/{assetId}/price-list")
    Call<PriceListItem> updatePriceListItem(@Path("assetId") Long assetId, @Body PriceListItemUpdate update);

    @Streaming
    @GET("assets/price-list/pdf")
    Call<ResponseBody> getPriceListPdf(@Query("type") String type);
}

