package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.event.EventRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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
    @GET("events/by-organizer")
    Call<PaginatedResponse<Event>> getEventsByOrganizer(
            @Query("page") int page,
            @Query("size") int size
    );
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("events")
    Call<Event> create(@Body EventRequest request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("events")
    Call<Event> update(@Body EventRequest request);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("events/{id}")
    Call<Object> delete(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/top5")
    Call<List<Event>> getTop5Events();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events")
    Call<PaginatedResponse<Event>> getAllEvents(
            @Query("minMaxGuests") Integer minMaxGuests,
            @Query("maxMaxGuests") Integer maxMaxGuests,
            @Query("dateFrom") String dateFrom,      // format yyyy-MM-dd
            @Query("dateTo") String dateTo,          // format yyyy-MM-dd
            @Query("types") List<Long> types,
            @Query("organizers") List<Long> organizers,
            @Query("addresses") List<String> addresses,
            @Query("minRating") Double minRating,
            @Query("maxRating") Double maxRating,
            @Query("showInPast") boolean showInPast,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );
}


