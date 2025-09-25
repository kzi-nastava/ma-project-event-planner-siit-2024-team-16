package com.example.evenmate.clients;

import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.PaginatedResponse;

import com.example.evenmate.models.category.CategoryRequest;
import com.example.evenmate.models.category.CategorySuggestion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("categories")
    Call<PaginatedResponse<Category>> getCategories();
    
    Call<PaginatedResponse<Category>> getCategories(@Query("page") Integer page, @Query("size") Integer size);

    @POST("categories")
    Call<Category> createCategory(@Body CategoryRequest request);

    @PUT("categories/{id}")
    Call<Category> updateCategory(@Path("id") Long id, @Body CategoryRequest request);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);

    @GET("categories/suggestions")
    Call<PaginatedResponse<CategorySuggestion>> getCategorySuggestions(@Query("page") Integer page, @Query("size") Integer size);

    @PUT("categories/{id}/approve")
    Call<Void> approveCategorySuggestion(@Path("id") Long id);
}
