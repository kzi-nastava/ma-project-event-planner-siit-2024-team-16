package com.example.evenmate.clients;

import com.example.evenmate.models.asset.Reservation;
import com.example.evenmate.models.asset.ReservationRequest;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.service.ServiceCreate;
import com.example.evenmate.models.service.ServiceFilter;
import com.example.evenmate.models.service.ServiceUpdate;
import com.example.evenmate.models.PaginatedResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ServiceService {
    @GET("services/{id}")
    Call<Service> getById(@Path("id") Long id);

    @GET("services")
    Call<PaginatedResponse<Service>> getAll(@Query("page") Integer page, @Query("size") Integer size, @QueryMap Map<String, String> filters);

    @GET("types")
    Call<PaginatedResponse<EventType>> getEventTypes(@Query("page") Integer page, @Query("size") Integer size);

    @POST("services")
    Call<Service> add(@Body ServiceCreate service);

    @PUT("services/{id}")
    Call<Service> update(@Path("id") Long id, @Body ServiceUpdate service);

    @DELETE("services/{id}")
    Call<Void> delete(@Path("id") Long id);

    @POST("/api/v1/reservations")
    Call<Reservation> createReservation(@Body ReservationRequest request);
}

