package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.budget.BudgetAsset;
import com.example.evenmate.models.budget.BudgetItem;
import com.example.evenmate.models.budget.BudgetItemCreate;
import com.example.evenmate.models.budget.BudgetItemUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BudgetService {
    @GET("budget")
    Call<PaginatedResponse<BudgetItem>> getAll(@Query("eventId") Long eventId, @Query("page") Integer page, @Query("size") Integer size);

    @GET("budget/{id}/assets")
    Call<PaginatedResponse<BudgetAsset>> getAssetsByBudgetItemId(@Path("id") Long id, @Query("page") Integer page, @Query("size") Integer size);

    @POST("budget")
    Call<BudgetItem> add(@Body BudgetItemCreate budgetItem);

    @PUT("budget/{id}")
    Call<BudgetItem> update(@Path("id") Long id, @Body BudgetItemUpdate budgetItem);

    @DELETE("budget/{id}")
    Call<Void> delete(@Path("id") Long id);
}
