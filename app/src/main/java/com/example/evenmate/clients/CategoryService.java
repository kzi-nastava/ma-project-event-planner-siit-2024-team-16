package com.example.evenmate.clients;

import com.example.evenmate.models.event.CategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CategoryService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("categories")
    Call<List<CategoryDTO>> getCategories();
}
