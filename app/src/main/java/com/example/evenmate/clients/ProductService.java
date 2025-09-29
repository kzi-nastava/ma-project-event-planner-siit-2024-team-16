package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.asset.ProductRequest;
import com.example.evenmate.models.asset.PurchaseRequest;

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
    @GET("products/by-provider")
    Call<PaginatedResponse<Product>> getProductsByProvider(
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
    @PUT("products/{id}")
    Call<Product> update(@Body ProductRequest request, @Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("products/{id}")
    Call<Object> delete(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("products/{productId}/favorite")
    Call<Void> favoriteProductToggle(@Path("productId") Long productId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("products/{productId}/favorite")
    Call<Boolean> checkIsProductFavorite(@Path("productId") Long productId);

    @GET("products/{productId}")
    Call<Product> getById(@Path("productId") Long productId);

    @GET("products/{productId}/favorite")
    Call<Boolean> isFavorite(@Path("productId") Long productId);

    @PUT("products/{productId}/favorite")
    Call<Void> toggleFavorite(@Path("productId") Long productId);

    @POST("products/{productId}/buy")
    Call<Void> buyProduct(@Path("productId") Long productId, @Body PurchaseRequest purchaseRequest);
}


