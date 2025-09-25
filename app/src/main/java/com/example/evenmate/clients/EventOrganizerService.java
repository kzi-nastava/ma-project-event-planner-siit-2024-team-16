package com.example.evenmate.clients;

import com.example.evenmate.models.user.EventOrganizer;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface EventOrganizerService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("organizers/for-filter")
    Call<List<EventOrganizer>> getAllOrganizers();
}
