package com.example.evenmate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.evenmate.clients.AuthService;
import com.example.evenmate.databinding.FragmentRegisterBinding;
import com.example.evenmate.models.User;
import com.example.evenmate.models.Company;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthService authService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        // Initialize switch and company layout visibility handling
        binding.switchProvider.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.companyInfoLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Set up register button click listener
        binding.btnRegister.setOnClickListener(v -> attemptRegistration());

        return binding.getRoot();
    }

    private void attemptRegistration() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Create user object
        User user = new User();
        user.setEmail(Objects.requireNonNull(binding.txtEmail.getText()).toString());
        user.setPassword(Objects.requireNonNull(binding.txtPassword.getText()).toString());
        user.setFirstName(Objects.requireNonNull(binding.txtFirstName.getText()).toString());
        user.setLastName(Objects.requireNonNull(binding.txtLastName.getText()).toString());
        user.getAddress().setCity(Objects.requireNonNull(binding.txtCity.getText()).toString());
        user.getAddress().setStreet(Objects.requireNonNull(binding.txtStreet.getText()).toString());
        user.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtStreetNumber.getText()).toString());
        user.setPhone(Objects.requireNonNull(binding.txtPhone.getText()).toString());
        //TODO: add photos

        // Add company info if provider switch is checked
        if (binding.switchProvider.isChecked()) {
            Company company = new Company();
            company.setEmail(Objects.requireNonNull(binding.txtCompanyEmail.getText()).toString());
            company.setName(Objects.requireNonNull(binding.txtCompanyName.getText()).toString());
            company.getAddress().setCity(Objects.requireNonNull(binding.txtCompanyCity.getText()).toString());
            company.getAddress().setStreet(Objects.requireNonNull(binding.txtCompanyStreet.getText()).toString());
            company.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtCompanyStreetNumber.getText()).toString());
            company.setPhone(Objects.requireNonNull(binding.txtCompanyPhone.getText()).toString());
            company.setDescription(Objects.requireNonNull(binding.txtDescription.getText()).toString());
            //TODO: add company photos
            user.setCompany(company);
        }

        // Make API call
        authService.registerUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration successful! Please check your email to activate your account.",
                            Toast.LENGTH_LONG).show();
                    // Navigate to login or home screen
                } else {
                    String errorMessage = "Registration failed: " +
                            (response.errorBody() != null ? response.errorBody().toString() : "Unknown error");
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        // Validate email
        if (Objects.requireNonNull(binding.txtEmail.getText()).toString().isEmpty()) {
            binding.txtFieldEmail.setError("Email is required");
            return false;
        }

        // Validate password
        if (Objects.requireNonNull(binding.txtPassword.getText()).toString().isEmpty()) {
            binding.txtFieldPasswordRegister.setError("Password is required");
            return false;
        }

        // Validate password confirmation
        if (!binding.txtPassword.getText().toString()
                .equals(Objects.requireNonNull(binding.txtConfirmPassword.getText()).toString())) {
            binding.txtFieldConfirmPasswordRegister.setError("Passwords do not match");
            return false;
        }

        // Validate company fields if provider switch is checked
        if (binding.switchProvider.isChecked()) {
            if (Objects.requireNonNull(binding.txtCompanyEmail.getText()).toString().isEmpty()) {
                binding.txtFieldCompanyEmail.setError("Company email is required");
                return false;
            }
            if (Objects.requireNonNull(binding.txtCompanyName.getText()).toString().isEmpty()) {
                binding.txtFieldCompanyName.setError("Company name is required");
                return false;
            }
            // Add other company field validations as needed
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}