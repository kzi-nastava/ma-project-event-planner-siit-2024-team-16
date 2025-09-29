package com.example.evenmate.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evenmate.MainActivity;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ImageUtils;

import java.time.Duration;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuickRegisterActivity extends AppCompatActivity {

    private String token;
    private EditText inputEmail, inputPassword, inputFirstName, inputLastName, inputPhone, inputCountry, inputCity, inputStreet, inputStreetNumber;
    private Button buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_registration);
        if (getIntent().getData() != null) {
            token = getIntent().getData().getQueryParameter("token");
        }else {
            finish();
        }
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputFirstName = findViewById(R.id.input_first_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputPhone = findViewById(R.id.input_phone);
        inputCountry = findViewById(R.id.input_country);
        inputCity = findViewById(R.id.input_city);
        inputStreet = findViewById(R.id.input_street);
        inputStreetNumber = findViewById(R.id.input_street_number);
        buttonRegister = findViewById(R.id.button_register);

        buttonRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        Address address = new Address();
        address.setCountry(Objects.requireNonNull(inputCountry.getText()).toString());
        address.setCity(Objects.requireNonNull(inputCity.getText()).toString());
        address.setStreetName(Objects.requireNonNull(inputStreet.getText()).toString());
        address.setStreetNumber(Objects.requireNonNull(inputStreetNumber.getText()).toString());

        User registerRequest = new User();
        registerRequest.setEmail(Objects.requireNonNull(inputEmail.getText()).toString());
        registerRequest.setPassword(Objects.requireNonNull(inputPassword.getText()).toString());
        registerRequest.setFirstName(Objects.requireNonNull(inputFirstName.getText()).toString());
        registerRequest.setLastName(Objects.requireNonNull(inputLastName.getText()).toString());
        registerRequest.setPhone(Objects.requireNonNull(inputPhone.getText()).toString());
        registerRequest.setAddress(address);

        sendRegisterRequest(registerRequest);
    }

    private void sendRegisterRequest(User request) {
        ClientUtils.authService.quickRegister(token,request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(QuickRegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Registration failed!";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
