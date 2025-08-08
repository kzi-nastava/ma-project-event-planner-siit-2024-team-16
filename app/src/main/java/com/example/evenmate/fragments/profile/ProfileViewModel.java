package com.example.evenmate.fragments.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.user.UpdateUserRequest;
import com.example.evenmate.models.user.User;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();

    public ProfileViewModel(){}

    public void update(UpdateUserRequest user) {
        Call<User> call = ClientUtils.userService.update(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    success.postValue("User successfully updated.");
                    AuthManager.loggedInUser = response.body();
                } else {
                    errorMessage.postValue("Failed to update user. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}

