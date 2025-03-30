package com.example.evenmate.clients;

import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.PaginatedResponse;

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
            @Query("size") int size
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("types")
    Call<EventType> createType(@Body EventType request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("types")
    Call<EventType> updateType(@Body EventType request);
}

