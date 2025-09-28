package com.example.evenmate.clients;

import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.service.Service;
import com.example.evenmate.models.asset.Product;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.user.Block;
import com.example.evenmate.models.user.CalendarItem;
import com.example.evenmate.models.user.Report;
import com.example.evenmate.models.user.UpdateUserRequest;
import com.example.evenmate.models.user.User;

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

public interface UserService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/whoami")
    Call<User> whoami();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{userId}/favorite-events/{eventId}/status")
    Call<Boolean> checkFavoriteStatus(@Path("userId") Long userId, @Path("eventId") Long eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{userId}/favorite-events")
    Call<PaginatedResponse<Event>> getFavoriteEvents(@Path("userId") Long userId, @Query("page") int page,
                                                     @Query("size") int size);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/calendar")
    Call<List<CalendarItem>> getCalendar();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @POST("users/{userId}/favorite-events/{eventId}/toggle")
    Call<Boolean> favoriteEventToggle(@Path("userId") Long userId, @Path("eventId") Long eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("users/{id}")
    Call<Object> delete(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("users")
    Call<User> update(@Body UpdateUserRequest request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{userId}/favorite-products")
    Call<PaginatedResponse<Product>> getFavoriteProducts(@Path("userId") Long userId, @Query("page") int page,
                                                     @Query("size") int size);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{userId}/favorite-services")
    Call<PaginatedResponse<Service>> getFavoriteServices(@Path("userId") Long userId, @Query("page") int page,
                                                         @Query("size") int size);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("users/{id}/report")
    Call<Report> reportUser(@Path("id") Long id, @Body String reportReason);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("users/block/{blocked_id}")
    Call<Block> blockUser(@Path("blocked_id") Long blockedId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/is-blocked/{id}")
    Call<Boolean> isBlocked(@Path("id") Long id);

    @GET("users/{userId}")
    Call<User> getById(@Path("userId") Long userId);

    @GET("users/pending-reports")
    Call<PaginatedResponse<Report>> getPendingReports(
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("users/approve-report/{id}")
    Call<Void> approveReport(@Path("id") Long reportId);

    @DELETE("users/report/{id}")
    Call<Void> deleteReport(@Path("id") Long reportId);
}
