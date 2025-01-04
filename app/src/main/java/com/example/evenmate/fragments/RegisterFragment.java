package com.example.evenmate.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentRegisterBinding;
import com.example.evenmate.models.user.User;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.utils.ToastUtils;
import com.example.evenmate.validation.ValidationField;
import com.example.evenmate.validation.ValidationRule;
import com.example.evenmate.validation.rules.EmailRule;
import com.example.evenmate.validation.rules.MatchPasswordRule;
import com.example.evenmate.validation.rules.MinLengthRule;
import com.example.evenmate.validation.rules.RequiredRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.os.Build;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private static final int MAX_COMPANY_IMAGES = 5;
    private String userImageBase64;
    private static final int MAX_IMAGE_SIZE = 800;
    private final List<String> companyImagesBase64 = new ArrayList<>();
    private LinearLayout companyImagesContainer;
    private final ActivityResultLauncher<String> userImagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchImagePicker(true, -1);
                } else {
                    Toast.makeText(requireContext(), "Permission required to select image", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), "Permission required to select image", Toast.LENGTH_SHORT).show();
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

        binding.switchProvider.setOnCheckedChangeListener((buttonView, isChecked) -> binding.companyInfoLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        binding.iconUpload.setOnClickListener(v -> checkPermissionAndPickImage(true, -1));

        binding.iconCompanyUpload.setOnClickListener(v -> {
            if (companyImagesBase64.size() < MAX_COMPANY_IMAGES) {
                checkPermissionAndPickImage(false, companyImagesBase64.size());
            } else {
                Toast.makeText(requireContext(), "Maximum " + MAX_COMPANY_IMAGES + " images allowed",
                        Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnRegister.setOnClickListener(v -> attemptRegistration());

        return binding.getRoot();
    }

    private void checkPermissionAndPickImage(boolean isUserImage, int imageIndex) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
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
                Bitmap originalBitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(requireContext().getContentResolver(), imageUri)
                );

                Bitmap resizedBitmap = resizeImage(originalBitmap);
                String base64Image = convertBitmapToBase64(resizedBitmap);

                if (isUserImage) {
                    userImageBase64 = base64Image;
                    binding.iconUpload.setImageBitmap(resizedBitmap);
                } else {
                    if (imageIndex >= 0 && imageIndex < companyImagesBase64.size()) {
                        companyImagesBase64.set(imageIndex, base64Image);
                    } else {
                        companyImagesBase64.add(base64Image);
                    }
                    updateCompanyImagesUI();
                }
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
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
        boolean hasAlpha = bitmap.hasAlpha();

        if (hasAlpha) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            return "data:image/png;base64," + base64String;
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            return "data:image/jpeg;base64," + base64String;
        }
    }

    private void updateCompanyImagesUI() {
        companyImagesContainer.removeAllViews();

        for (int i = 0; i < companyImagesBase64.size(); i++) {
            final int index = i;
            String base64Image = companyImagesBase64.get(i);

            LinearLayout imageContainer = new LinearLayout(requireContext());
            imageContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(100));
            containerParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
            imageContainer.setLayoutParams(containerParams);

            ImageView imageView = new ImageView(requireContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    dpToPx(80), dpToPx(80));
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Fix: Properly handle the data URL format
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }

            try {
                byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedBitmap != null) {
                    imageView.setImageBitmap(decodedBitmap);
                } else {
                    imageView.setImageResource(R.drawable.ic_error);
                    Toast.makeText(requireContext(), "Failed to decode image", Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                imageView.setImageResource(R.drawable.ic_error);
                Toast.makeText(requireContext(), "Invalid image format", Toast.LENGTH_SHORT).show();
            }

            ImageView deleteButton = new ImageView(requireContext());
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    dpToPx(20), dpToPx(20));
            deleteParams.gravity = Gravity.CENTER;
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setImageResource(R.drawable.ic_red_delete);
            deleteButton.setOnClickListener(v -> {
                companyImagesBase64.remove(index);
                updateCompanyImagesUI();
            });

            imageContainer.addView(imageView);
            imageContainer.addView(deleteButton);
            companyImagesContainer.addView(imageContainer);
        }

        binding.iconCompanyUpload.setVisibility(
                companyImagesBase64.size() < MAX_COMPANY_IMAGES ? View.VISIBLE : View.GONE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void attemptRegistration() {
        if (!validateInputs()) {
            return;
        }
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

        if (binding.switchProvider.isChecked()) {
            Company company = new Company();
            company.setEmail(Objects.requireNonNull(binding.txtCompanyEmail.getText()).toString());
            company.setName(Objects.requireNonNull(binding.txtCompanyName.getText()).toString());
            company.getAddress().setCity(Objects.requireNonNull(binding.txtCompanyCity.getText()).toString());
            company.getAddress().setStreetName(Objects.requireNonNull(binding.txtCompanyStreet.getText()).toString());
            company.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtCompanyStreetNumber.getText()).toString());
            company.setPhone(Objects.requireNonNull(binding.txtCompanyPhone.getText()).toString());
            company.setDescription(Objects.requireNonNull(binding.txtDescription.getText()).toString());
            company.setPhotos(companyImagesBase64);
            user.setCompany(company);
        }
        register(user);
    }

    private void register(User user){
        retrofit2.Call<User> call = ClientUtils.authService.register(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    handleRegistrationSuccess();
                } else {
                    String errorBody;
                    try (ResponseBody responseBody = response.errorBody()) {
                        errorBody = responseBody != null ? responseBody.string() : null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (errorBody != null) {
                        if (errorBody.contains(getString(R.string.email_already_exists))) {
                            errorBody = getString(R.string.account_already_exists_email);
                        } else if (errorBody.contains(getString(R.string.company_already_exists))) {
                            errorBody = getString(R.string.company_already_exists);
                        } else {
                            errorBody = getString(R.string.registration_failed_check_your_information);
                        }
                    } else {
                        errorBody = getString(R.string.registration_failed_try_again);
                    }
                    handleRegistrationError(errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.network_error) + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleRegistrationSuccess() {
        ToastUtils.showCustomToast(requireContext(),
                getString(R.string.registration_successful),
                false);

        requireActivity().runOnUiThread(() -> new Handler().postDelayed(() -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
            navController.popBackStack();
        }, 1000));
    }

    private void handleRegistrationError(String errorMessage) {
        ToastUtils.showCustomToast(requireContext(),
                getString(R.string.registration_failed) + errorMessage,
                true);
    }
    private List<ValidationField> getUserValidationFields() {
        return Arrays.asList(
                new ValidationField(binding.txtFieldEmail, binding.txtEmail, getString(R.string.email),
                        new RequiredRule(), new EmailRule()),
                new ValidationField(binding.txtFieldPasswordRegister, binding.txtPassword, getString(R.string.password),
                        new RequiredRule(), new MinLengthRule(8)),
                new ValidationField(binding.txtFieldConfirmPasswordRegister, binding.txtConfirmPassword, getString(R.string.confirm_password),
                        new RequiredRule(), new MatchPasswordRule(binding.txtPassword)),
                new ValidationField(binding.txtFieldCity, binding.txtCity, getString(R.string.city),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldStreet, binding.txtStreet, getString(R.string.street),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldStreetNumber, binding.txtStreetNumber, getString(R.string.street_number),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldPhone, binding.txtPhone, getString(R.string.phone),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldFirstName, binding.txtFirstName, getString(R.string.first_name),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldLastName, binding.txtLastName, getString(R.string.last_name),
                        new RequiredRule())
        );
    }

    private List<ValidationField> getCompanyValidationFields() {
        return Arrays.asList(
                new ValidationField(binding.txtFieldCompanyEmail, binding.txtCompanyEmail, getString(R.string.company_email),
                        new RequiredRule(), new EmailRule()),
                new ValidationField(binding.txtFieldCompanyName, binding.txtCompanyName, getString(R.string.company_name),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldCompanyCity, binding.txtCompanyCity, getString(R.string.company_city),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldCompanyStreet, binding.txtCompanyStreet, getString(R.string.company_street),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldCompanyStreetNumber, binding.txtCompanyStreetNumber, getString(R.string.company_street_number),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldCompanyPhone, binding.txtCompanyPhone, getString(R.string.company_phone),
                        new RequiredRule()),
                new ValidationField(binding.txtFieldDescription, binding.txtDescription, getString(R.string.company_description),
                        new RequiredRule())
        );
    }

    private boolean validateFields(List<ValidationField> fields) {
        boolean isValid = true;

        for (ValidationField field : fields) {
            String value = Objects.requireNonNull(field.getInput().getText()).toString();
            field.getLayout().setError(null);

            for (ValidationRule rule : field.getRules()) {
                if (!rule.validate(value)) {
                    field.getLayout().setError(field.getFieldName() + " " + rule.getErrorMessage());
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    private boolean validateInputs() {
        boolean isValid = validateFields(getUserValidationFields());

        if (binding.switchProvider.isChecked()) {
            isValid = validateFields(getCompanyValidationFields()) && isValid;
        }

        return isValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}