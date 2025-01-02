package com.example.evenmate.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentLoginBinding;
import com.example.evenmate.models.user.LoginRequest;
import com.example.evenmate.models.user.TokenResponse;
import com.example.evenmate.models.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtSignUp.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_login_to_registerFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void login(LoginRequest request){
        retrofit2.Call<TokenResponse> call = ClientUtils.authService.login(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save token
                    SharedPreferences prefs = ClientUtils.getContext().getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE);
                    prefs.edit().putString("jwt_token", response.body().getAccessToken()).apply();

                    // Get user info
                    getUserInfo();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // Handle error
            }
        });

    }

    private void getUserInfo() {
        retrofit2.Call<User> call = ClientUtils.userService.whoami();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Handle user info
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Handle error
            }
        });
    }
}