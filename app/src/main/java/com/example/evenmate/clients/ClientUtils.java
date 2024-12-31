package com.example.evenmate.clients;

import java.util.concurrent.TimeUnit;
import com.example.evenmate.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String BASE_URL = "http://"+ BuildConfig.IP_ADDR +":8080";
    public static final String SERVICE_API_PATH = BASE_URL + "/api/v1/";

    public static OkHttpClient test(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    // Adding custom header
                    Request request = original.newBuilder()
                            .header("X-Base-URL", BASE_URL)
                            .build();
                    return chain.proceed(request);
                })
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(test())
            .build();

    public static EventTypeService eventTypeService = retrofit.create(EventTypeService.class);
    public static AuthService authService = retrofit.create(AuthService.class);
}


