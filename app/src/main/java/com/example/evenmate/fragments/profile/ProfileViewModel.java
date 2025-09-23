package com.example.evenmate.fragments.profile;

import android.util.Log;

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
    private final MutableLiveData<Boolean> deleteFailed = new MutableLiveData<>(null);

    public void resetDeleteFailed() { deleteFailed.setValue(false); }
    public void resetMessages(){
        this.success.setValue(null);
        this.errorMessage.setValue(null);
    }
    public void setUser(User user){
        this.user.setValue(user);
    }

    public ProfileViewModel(){ }

    public void update(UpdateUserRequest user) {
        Call<User> call = ClientUtils.userService.update(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    success.postValue("User successfully updated.");
                    AuthManager.loggedInUser = response.body();
                    setUser(response.body());
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

    public void delete(Long id) {
        Call<Object> call = ClientUtils.userService.delete(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.d("delete", "deleted");
                    deleteFailed.setValue(false);
                } else {
                    Log.e("delete", "Delete failed: " + response.code() + " " + response.message());
                    deleteFailed.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Error deleting user: " + t.getMessage());
                deleteFailed.setValue(true);
            }
        });
    }
}

