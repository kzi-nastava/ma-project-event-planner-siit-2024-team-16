package com.example.evenmate.clients;

import com.example.evenmate.models.user.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("users/whoami")
    Call<User> whoami();
}
