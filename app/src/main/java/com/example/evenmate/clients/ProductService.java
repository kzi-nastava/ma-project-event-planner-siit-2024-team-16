package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.asset.ProductRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products")
    Call<PaginatedResponse<Product>> getProducts(
            @Query("page") int page,
            @Query("size") int size
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/by-organizer")
    Call<PaginatedResponse<Product>> getProductsByOrganizer(
            @Query("page") int page,
            @Query("size") int size
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("products")
    Call<Product> create(@Body ProductRequest request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("products")
    Call<Product> update(@Body ProductRequest request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("products/{id}")
    Call<Object> delete(@Path("id") Long id);
}


