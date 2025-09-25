package com.example.evenmate.fragments.event;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.PaginatedResponse;
import com.example.evenmate.models.event.Event;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
@NoArgsConstructor
public class EventsViewModel extends ViewModel {
    private static final int PAGE_SIZE = 5;
    @Setter
    private String fetchMode = "ALL_EVENTS";
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> totalPages = new MutableLiveData<>(1);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteFailed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> favoriteStatus = new MutableLiveData<>();

    public void resetDeleteFailed() { deleteFailed.setValue(false); }

    public void fetchEvents() {
        int apiPage = currentPage.getValue() != null ? currentPage.getValue() - 1 : 0;
        Call<PaginatedResponse<Event>> call;
        if ("FAVORITES".equals(fetchMode)) {
            call = ClientUtils.userService.getFavoriteEvents(AuthManager.loggedInUser.getId(), apiPage, PAGE_SIZE);
        } else if ("YOUR_EVENTS".equals(fetchMode)) {
            call = ClientUtils.eventService.getEventsByOrganizer(apiPage, PAGE_SIZE);
        } else {
            call = ClientUtils.eventService.getEvents(apiPage, PAGE_SIZE);
        }
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PaginatedResponse<Event>> call, @NonNull Response<PaginatedResponse<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.setValue(response.body().getContent());
                    totalPages.setValue(response.body().getTotalPages());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaginatedResponse<Event>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error fetching events: " + t.getMessage());
            }
        });
    }

    public void deleteEvent(Long id) {
        Call<Void> call = ClientUtils.eventService.delete(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                fetchEvents();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error deleting event: " + t.getMessage());
                deleteFailed.setValue(true);
            }
        });
    }
    public void getEvent(Long id) {
        Call<Event> call = ClientUtils.eventService.getById(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event.setValue(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable throwable) {

            }
        });
    }

    public void checkFavoriteStatus(Long userId, Long eventId) {
        retrofit2.Call<Boolean> call = ClientUtils.userService.checkFavoriteStatus(userId, eventId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null)
                    favoriteStatus.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
    public void changeFavoriteStatus(Long userId, Long eventId) {
        retrofit2.Call<Boolean> call = ClientUtils.userService.favoriteEventToggle(userId, eventId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null)
                    favoriteStatus.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }


    public void nextPage() {
        Integer current = currentPage.getValue();
        Integer total = totalPages.getValue();
        if (current != null && total != null && current < total) {
            currentPage.setValue(current + 1);
            fetchEvents();
        }
    }

    public void previousPage() {
        Integer current = currentPage.getValue();
        if (current != null && current > 1) {
            currentPage.setValue(current - 1);
            fetchEvents();
        }
    }

}