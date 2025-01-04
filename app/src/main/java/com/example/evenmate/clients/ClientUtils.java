package com.example.evenmate.clients;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import com.example.evenmate.BuildConfig;
import com.example.evenmate.interceptors.AuthInterceptor;
import com.example.evenmate.models.user.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String BASE_URL = "http://" + BuildConfig.IP_ADDR + ":8080";
    public static final String SERVICE_API_PATH = BASE_URL + "/api/v1/";
    private static Context context;
    private static Retrofit retrofit;
    public static User loggedInUser = null;

    public static EventTypeService eventTypeService;
    public static AuthService authService;
    public static UserService userService;

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
        initializeServices();
    }

    private static void initializeServices() {
        eventTypeService = getRetrofit().create(EventTypeService.class);
        authService = getRetrofit().create(AuthService.class);
        userService = getRetrofit().create(UserService.class);
    }

    public static Context getContext() {
        return context;
    }

    private static OkHttpClient createHttpClient() {
        if (context == null) {
            throw new IllegalStateException("Context must be initialized before creating client");
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(context))
                .build();
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVICE_API_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createHttpClient())
                    .build();
        }
        return retrofit;
    }
}