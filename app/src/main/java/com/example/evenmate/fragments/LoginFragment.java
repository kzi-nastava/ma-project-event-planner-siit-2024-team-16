package com.example.evenmate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentLoginBinding;
import com.example.evenmate.models.user.LoginRequest;
import com.example.evenmate.models.user.TokenResponse;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
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
        binding.btnLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.txtEmailLogin.getText()).toString();
            String password = Objects.requireNonNull(binding.txtPasswordLogin.getText()).toString();
            LoginRequest request = new LoginRequest(email, password);
            login(request);
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
                    AuthManager authManager = AuthManager.getInstance(requireContext());
                    authManager.saveToken( response.body().getAccessToken());
                    getUserInfo();
                }else {
                    String errorBody;
                    try (ResponseBody responseBody = response.errorBody()) {
                        errorBody = responseBody != null ? responseBody.string() : null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (errorBody != null) {
                        if (errorBody.contains(getString(R.string.your_account_is_not_activated))) {
                            errorBody = getString(R.string.your_account_is_not_activated_please_activate);
                        } else if (errorBody.contains(getString(R.string.invalid_credentials))){
                            errorBody = getString(R.string.invalid_credentials);
                        }
                        else {
                            errorBody = getString(R.string.login_failed);
                        }
                    } else {
                        errorBody = getString(R.string.login_failed_please_try_again_later);
                    }
                    ToastUtils.showCustomToast(requireContext(), errorBody, true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(requireContext(), getString(R.string.network_error) + t.getMessage(), true);
            }
        });

    }

    private void getUserInfo() {
        retrofit2.Call<User> call = ClientUtils.userService.whoami();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.loggedInUser = response.body();
                    requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
                        NavController navController = Navigation.findNavController(requireView());
                        navController.popBackStack();
                    }, 1000));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                ToastUtils.showCustomToast(requireContext(),getString(R.string.network_error) + t.getMessage(), true);
            }
        });
    }
}