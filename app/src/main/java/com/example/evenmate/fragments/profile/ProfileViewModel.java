package com.example.evenmate.fragments.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();


    public User getUser(){return user.getValue();}
    public String getErrorMessage(){return errorMessage.getValue();}
    public String getSuccess(){return success.getValue();}

    public ProfileViewModel(){}

    public void update(User user) {
        Call<User> call = ClientUtils.userService.update(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    success.postValue("User successfully updated.");
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

