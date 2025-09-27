package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Asset;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AssetService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("assets")
    Call<PaginatedResponse<Asset>> getAll(
            @Query("addresses") List<String> addresses,
            @Query("categories") List<Long> categories,
            @Query("eventTypes") List<Long> eventTypes,
            @Query("providers") List<Long> providers,
            @Query("availableFrom") String availableFrom,
            @Query("availableTo") String availableTo,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("minRating") Double minRating,
            @Query("maxRating") Double maxRating,
            @Query("type") String type,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("assets/top5")
    Call<List<Asset>> getTop5();
    
    @GET("assets/search")
    Call<PaginatedResponse<Asset>> search(
            @Query("keywords") String keywords,
            @Query("page") int page,
            @Query("size") int size
    );
    
    @PUT("assets/{assetId}/category")
    Call<Asset> updateAssetCategory(@Path("assetId") Long assetId, @Query("categoryId") Long categoryId);

    @GET("assets/{assetId}")
    Call<Asset> getById(@Path("assetId") Long assetId);
}
