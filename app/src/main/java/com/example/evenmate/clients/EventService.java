package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.Event;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface EventService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events")
    Call<PaginatedResponse<Event>> getEvents(
            @Query("page") int page,
            @Query("size") int size
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("events")
    Call<Event> createEvent(@Body Event request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("events")
    Call<Event> updateEvent(@Body Event request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("events")
    void delete(@Body Event request);
}


