package com.example.evenmate.interceptors;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import com.example.evenmate.auth.AuthManager;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (originalRequest.url().toString().contains("auth/login")
                || originalRequest.url().toString().contains("auth/register")) {
            return chain.proceed(originalRequest);
        }

        String token = AuthManager.getInstance(context).getToken();

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}
