package com.example.evenmate.clients;

import com.example.evenmate.models.asset.Asset;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AssetService {
    @PUT("assets/{assetId}/category")
    Call<Asset> updateAssetCategory(@Path("assetId") Long assetId, @Query("categoryId") Long categoryId);
}
