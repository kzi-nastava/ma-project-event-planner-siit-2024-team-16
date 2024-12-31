package com.example.evenmate.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentRegisterBinding;
import com.example.evenmate.models.User;
import com.example.evenmate.models.Company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Build;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private static final int MAX_COMPANY_IMAGES = 5;
    private String userImageBase64;
    private static final int MAX_IMAGE_SIZE = 800;
    private List<String> companyImagesBase64 = new ArrayList<>();
    private LinearLayout companyImagesContainer;
    private final ActivityResultLauncher<String> userImagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchImagePicker(true, -1);
                } else {
                    Toast.makeText(getContext(), "Permission required to select image", Toast.LENGTH_SHORT).show();
                }
            });
    private final ActivityResultLauncher<Intent> userImagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleImageResult(result.getData(), true, -1);
                }
            });
    private final ActivityResultLauncher<String> companyImagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchImagePicker(false, companyImagesBase64.size());
                } else {
                    Toast.makeText(getContext(), "Permission required to select image", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> companyImagePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                int imageIndex = extras != null ? extras.getInt("image_index", -1) : -1;
                handleImageResult(result.getData(), false, imageIndex);
            }
        });
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        companyImagesContainer = binding.companyImagesContainer;

        // Initialize switch and company layout visibility handling
        binding.switchProvider.setOnCheckedChangeListener((buttonView, isChecked) -> binding.companyInfoLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        binding.iconUpload.setOnClickListener(v -> checkPermissionAndPickImage(true, -1));

        binding.iconCompanyUpload.setOnClickListener(v -> {
            if (companyImagesBase64.size() < MAX_COMPANY_IMAGES) {
                checkPermissionAndPickImage(false, companyImagesBase64.size());
            } else {
                Toast.makeText(getContext(), "Maximum " + MAX_COMPANY_IMAGES + " images allowed",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Set up register button click listener
        binding.btnRegister.setOnClickListener(v -> attemptRegistration());

        return binding.getRoot();
    }

    private void checkPermissionAndPickImage(boolean isUserImage, int imageIndex) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else { // Android 12 and below
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (isUserImage) {
                userImagePermissionLauncher.launch(permission);
            } else {
                companyImagePermissionLauncher.launch(permission);
            }
        } else {
            launchImagePicker(isUserImage, imageIndex);
        }
    }

    private void launchImagePicker(boolean isUserImage, int imageIndex) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (isUserImage) {
            userImagePickerLauncher.launch(intent);
        } else {
            Bundle extras = new Bundle();
            extras.putInt("image_index", imageIndex);
            intent.putExtras(extras);
            companyImagePickerLauncher.launch(intent);
        }
    }

    private void handleImageResult(Intent data, boolean isUserImage, int imageIndex) {
        try {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().getContentResolver(), imageUri);

                Bitmap resizedBitmap = resizeImage(originalBitmap);
                String base64Image = convertBitmapToBase64(resizedBitmap);

                if (isUserImage) {
                    userImageBase64 = base64Image;
                    binding.iconUpload.setImageBitmap(resizedBitmap);
                } else {
                    if (imageIndex >= 0 && imageIndex < companyImagesBase64.size()) {
                        // Replace existing image
                        companyImagesBase64.set(imageIndex, base64Image);
                    } else {
                        // Add new image
                        companyImagesBase64.add(base64Image);
                    }
                    updateCompanyImagesUI();
                }
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Bitmap resizeImage(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return original;
        }

        float ratio = Math.min(
                (float) MAX_IMAGE_SIZE / width,
                (float) MAX_IMAGE_SIZE / height
        );

        int finalWidth = Math.round(width * ratio);
        int finalHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(original, finalWidth, finalHeight, true);
    }
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Check if the bitmap has an alpha channel
        boolean hasAlpha = bitmap.hasAlpha();

        if (hasAlpha) {
            // Use PNG for images with transparency
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            Log.d("ImageUpload", "Base64 string start: " + base64String.substring(0, Math.min(base64String.length(), 100)));
            return "data:image/png;base64," + base64String;
        } else {
            // Use JPEG for images without transparency
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            Log.d("ImageUpload", "Base64 string start: " + base64String.substring(0, Math.min(base64String.length(), 100)));
            return "data:image/jpeg;base64," + base64String;
        }
    }
    private void updateCompanyImagesUI() {
        companyImagesContainer.removeAllViews();

        for (int i = 0; i < companyImagesBase64.size(); i++) {
            final int index = i;
            String base64Image = companyImagesBase64.get(i);

            // Create image container
            LinearLayout imageContainer = new LinearLayout(getContext());
            imageContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(100));
            containerParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
            imageContainer.setLayoutParams(containerParams);

            // Create ImageView for the company image
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(80));
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Set the image
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedBitmap);

            // Create delete button
            ImageView deleteButton = new ImageView(getContext());
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    dpToPx(20), dpToPx(20));
            deleteParams.gravity = Gravity.CENTER;
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setImageResource(R.drawable.ic_delete); // Make sure to have this icon
            deleteButton.setOnClickListener(v -> {
                companyImagesBase64.remove(index);
                updateCompanyImagesUI();
            });

            // Add views to container
            imageContainer.addView(imageView);
            imageContainer.addView(deleteButton);
            companyImagesContainer.addView(imageContainer);
        }

        // Update add button visibility
        binding.iconCompanyUpload.setVisibility(
                companyImagesBase64.size() < MAX_COMPANY_IMAGES ? View.VISIBLE : View.GONE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
        user.getAddress().setStreetName(Objects.requireNonNull(binding.txtStreet.getText()).toString());
        user.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtStreetNumber.getText()).toString());
        user.setPhone(Objects.requireNonNull(binding.txtPhone.getText()).toString());
        user.setPhoto(userImageBase64);
        // Add company info if provider switch is checked
        if (binding.switchProvider.isChecked()) {
            Company company = new Company();
            company.setEmail(Objects.requireNonNull(binding.txtCompanyEmail.getText()).toString());
            company.setName(Objects.requireNonNull(binding.txtCompanyName.getText()).toString());
            company.getAddress().setCity(Objects.requireNonNull(binding.txtCompanyCity.getText()).toString());
            company.getAddress().setStreetName(Objects.requireNonNull(binding.txtCompanyStreet.getText()).toString());
            company.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtCompanyStreetNumber.getText()).toString());
            company.setPhone(Objects.requireNonNull(binding.txtCompanyPhone.getText()).toString());
            company.setDescription(Objects.requireNonNull(binding.txtDescription.getText()).toString());
            company.setPhotos(companyImagesBase64); // Set all company images
            user.setCompany(company);
        }
        register(user);
    }

    private void register(User user){
        if (user.getPhoto() != null) {
            Log.d("ImageUpload", "User photo length: " + user.getPhoto().length());
            Log.d("ImageUpload", "Photo starts with: " + user.getPhoto().substring(0, Math.min(user.getPhoto().length(), 100)));
        }
        retrofit2.Call<User> call = ClientUtils.authService.registerUser(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration successful! Please check your email to activate your account.",
                            Toast.LENGTH_LONG).show();
                    // Navigate to login or home screen
                } else {
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.e("ImageUpload", "Registration failed. Error: " + errorBody);
                    Toast.makeText(getContext(), "Registration failed: " + errorBody,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("ImageUpload", "Network error", t);
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