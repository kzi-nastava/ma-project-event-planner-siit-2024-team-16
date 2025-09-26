package com.example.evenmate.clients;

import com.example.evenmate.models.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface AddressService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("address/for-filter")
    Call<List<Location>> getAllAddresses();
}
