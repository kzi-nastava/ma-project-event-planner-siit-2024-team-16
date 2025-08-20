package com.example.evenmate.clients;

import com.example.evenmate.models.user.UpdateUserRequest;
import com.example.evenmate.models.user.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
    @DELETE("users/{id}")
    Call<Object> delete(@Path("id") Long id);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("users")
    Call<User> update(@Body UpdateUserRequest request);
}
