package com.example.evenmate.clients;

import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.EventTypeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface EventTypeService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("types")
    Call<PaginatedResponse<EventType>> getTypes(
            @Query("page") int page,
            @Query("size") int size,
            @Query("active") boolean activeOnly
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("types")
    Call<PaginatedResponse<EventType>> getActiveTypes(
            @Query("active") boolean activeOnly
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("types")
    Call<EventType> createType(@Body EventTypeRequest request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("types")
    Call<EventType> updateType(@Body EventTypeRequest request);
}

