package com.example.evenmate.fragments.auth;

import static com.example.evenmate.utils.ImageUtils.MAX_COMPANY_IMAGES;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.evenmate.R;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentRegisterBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.User;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.utils.ImageUtils;
import com.example.evenmate.utils.ToastUtils;
import com.example.evenmate.validation.ValidationField;
import com.example.evenmate.validation.ValidationRule;
import com.example.evenmate.validation.rules.EmailRule;
import com.example.evenmate.validation.rules.MatchPasswordRule;
import com.example.evenmate.validation.rules.MinLengthRule;
import com.example.evenmate.validation.rules.RequiredRule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RegisterFragment extends Fragment implements ImageUtils.ImageHandlerCallback {

    private FragmentRegisterBinding binding;
    private String userImageBase64;
    private final List<String> companyImagesBase64 = new ArrayList<>();
    private LinearLayout companyImagesContainer;
    private ImageUtils userImageUtils;
    private ImageUtils companyImageUtils;
    private boolean isUserImageSelection = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        // Initialize image handlers
        userImageUtils = new ImageUtils(this, this);
        companyImageUtils = new ImageUtils(this, this);

        companyImagesContainer = binding.companyImagesContainer;

        binding.switchProvider.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.companyInfoLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        binding.iconUpload.setOnClickListener(v -> {
            isUserImageSelection = true;
            userImageUtils.selectImage();
        });

        binding.iconCompanyUpload.setOnClickListener(v -> {
            if (companyImagesBase64.size() < MAX_COMPANY_IMAGES) {
                isUserImageSelection = false;
                companyImageUtils.selectImage();
            } else {
                Toast.makeText(requireContext(), "Maximum " + MAX_COMPANY_IMAGES + " images allowed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRegister.setOnClickListener(v -> attemptRegistration());

        return binding.getRoot();
    }
    @Override
    public void onImageSelected(String base64Image, Bitmap bitmap) {
        if (isUserImageSelection) {
            userImageBase64 = base64Image;
            binding.iconUpload.setImageBitmap(bitmap);
        } else {
            companyImagesBase64.add(base64Image);
            updateCompanyImagesUI();
        }
    }

    @Override
    public void onImageError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
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

            // Use the utility method to set image
            ImageUtils.setImageFromBase64(imageView, base64Image);

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
        user.setAddress(new Address());
        user.getAddress().setCountry(Objects.requireNonNull(binding.txtCountry.getText()).toString());
        user.getAddress().setCity(Objects.requireNonNull(binding.txtCity.getText()).toString());
        user.getAddress().setStreetName(Objects.requireNonNull(binding.txtStreet.getText()).toString());
        user.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtStreetNumber.getText()).toString());
        user.setPhone(Objects.requireNonNull(binding.txtPhone.getText()).toString());
        user.setPhoto(userImageBase64);

        if (binding.switchProvider.isChecked()) {
            Company company = new Company();
            company.setEmail(Objects.requireNonNull(binding.txtCompanyEmail.getText()).toString());
            company.setName(Objects.requireNonNull(binding.txtCompanyName.getText()).toString());
            company.getAddress().setCountry(Objects.requireNonNull(binding.txtCountry.getText()).toString());
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

    private void register(User user) {
        retrofit2.Call<User> call = ClientUtils.authService.register(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    handleRegistrationSuccess();
                } else {

                    String errorMessage = getString(R.string.registration_failed_try_again);

                    try {
                        if (response.errorBody() != null) {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            if (json.has("message")) {
                                errorMessage = json.getString("message");
                            }
                        }
                    } catch (Exception ignored) { }

                    ToastUtils.showCustomToast(requireContext(), errorMessage, true);
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
                new ValidationField(binding.txtFieldCountry, binding.txtCountry, getString(R.string.country),
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
                new ValidationField(binding.txtFieldCompanyCountry, binding.txtCompanyCountry, getString(R.string.company_country),
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