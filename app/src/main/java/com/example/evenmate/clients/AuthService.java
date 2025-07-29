package com.example.evenmate.clients;

import static com.example.evenmate.clients.ClientUtils.BASE_URL;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.example.evenmate.models.user.LoginRequest;
import com.example.evenmate.models.user.TokenResponse;
import com.example.evenmate.models.user.User;

public interface AuthService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "X-Base-URL: " + BASE_URL
    })
    @POST("auth/register")
    Call<User> register(@Body User request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @POST("auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);
}
