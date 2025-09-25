package com.example.evenmate.clients;

import com.example.evenmate.models.user.EventOrganizer;
import com.example.evenmate.models.user.ProductServiceProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ProviderService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("providers/for-filter")
    Call<List<ProductServiceProvider>> getAll();
}
