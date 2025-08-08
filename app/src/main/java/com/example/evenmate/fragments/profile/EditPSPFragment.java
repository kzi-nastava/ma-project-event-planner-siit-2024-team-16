package com.example.evenmate.fragments.profile;

import static com.example.evenmate.utils.ImageUtils.MAX_COMPANY_IMAGES;
import static com.example.evenmate.utils.ImageUtils.setImageFromBase64;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.evenmate.R;
import com.example.evenmate.databinding.FragmentEditPspBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.models.user.UpdateCompanyRequest;
import com.example.evenmate.models.user.UpdateUserRequest;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ImageUtils;
import com.example.evenmate.utils.ToastUtils;
import com.example.evenmate.validation.ValidationField;
import com.example.evenmate.validation.ValidationRule;
import com.example.evenmate.validation.rules.MatchPasswordRule;
import com.example.evenmate.validation.rules.OptionalMinLengthRule;
import com.example.evenmate.validation.rules.RequiredOldPasswordRule;
import com.example.evenmate.validation.rules.RequiredRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditPSPFragment extends Fragment implements ImageUtils.ImageHandlerCallback {
    private FragmentEditPspBinding binding;
    private ProfileViewModel viewModel;
    private String userImageBase64;
    private List<String> companyImagesBase64 = new ArrayList<>();
    private ImageUtils companyImageUtils;
    private ImageUtils userImageUtils;
    private boolean isUserImageSelection = true;
    private LinearLayout companyImagesContainer;
    private LinearLayout userImageContainer;

    private User user;

    public EditPSPFragment() {
        this.user = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        companyImageUtils = new ImageUtils(this, this);
        userImageUtils = new ImageUtils(this, this);

        if(getArguments() != null){
            user = getArguments().getParcelable("user");
            if (user != null) {
                userImageBase64 = user.getPhoto();
                companyImagesBase64 = user.getCompany().getPhotos();
            }
        }
        binding = FragmentEditPspBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        companyImagesContainer = binding.companyImagesContainer;
        userImageContainer = binding.userImageContainer;

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
        binding.txtPasswordChange.setOnClickListener(v -> {
            binding.txtFieldNewPassword.setVisibility(View.VISIBLE);
            binding.txtFieldOldPassword.setVisibility(View.VISIBLE);
            binding.txtFieldConfirmNewPassword.setVisibility(View.VISIBLE);
        });

        updateProfileSection();
        setupSaveButton();
        observeViewModel();
    }
    private void updateProfileSection() {
        binding.txtFirstName.setText(user.getFirstName());
        binding.txtLastName.setText(user.getLastName());
        binding.txtPhone.setText(user.getPhone());
        if (user.getAddress() != null) {
            binding.txtCountry.setText(user.getAddress().getCountry());
            binding.txtCity.setText(user.getAddress().getCity());
            binding.txtStreet.setText(user.getAddress().getStreetName());
            binding.txtStreetNumber.setText(user.getAddress().getStreetNumber());
        }
        if(user.getCompany() != null)
            updateCompanySection(user.getCompany());
        updateUserImageUI();

    }
    private void updateCompanySection(Company company) {
        binding.txtCompanyName.setText(company.getName());
        binding.txtCompanyPhone.setText(company.getPhone());

        if (company.getAddress() != null) {
            binding.txtCompanyCountry.setText(company.getAddress().getCountry());
            binding.txtCompanyCity.setText(company.getAddress().getCity());
            binding.txtCompanyStreet.setText(company.getAddress().getStreetName());
            binding.txtCompanyStreetNumber.setText(company.getAddress().getStreetNumber());
        }

        binding.txtDescription.setText(company.getDescription());
        updateCompanyImagesUI();
    }

    private void updateUserImageUI() {
        userImageContainer.removeAllViews();

        if (userImageBase64 == null || userImageBase64.isEmpty()) {
            userImageContainer.setVisibility(View.GONE);
            return;
        }

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

        setImageFromBase64(imageView, userImageBase64);

        ImageView deleteButton = new ImageView(requireContext());
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                dpToPx(20), dpToPx(20));
        deleteParams.gravity = Gravity.CENTER;
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(R.drawable.ic_red_delete);
        deleteButton.setOnClickListener(v -> {
            userImageBase64 = null;
            updateUserImageUI();
            binding.iconUpload.setImageResource(R.drawable.ic_upload_image); // resetuj upload ikonu
        });

        imageContainer.addView(imageView);
        imageContainer.addView(deleteButton);
        userImageContainer.addView(imageContainer);
        userImageContainer.setVisibility(View.VISIBLE);
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

            setImageFromBase64(imageView, base64Image);

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
    private void setupSaveButton() {
        binding.btnSavePsp.setOnClickListener(v -> {
            if (validateInputs()) {
                UpdateUserRequest updatedUser = new UpdateUserRequest();
                updatedUser.setId(user.getId());
                updatedUser.setOldPassword(Objects.requireNonNull(binding.txtPasswordOld.getText()).toString().trim());
                updatedUser.setFirstName(Objects.requireNonNull(binding.txtFirstName.getText()).toString().trim());
                updatedUser.setLastName(Objects.requireNonNull(binding.txtLastName.getText()).toString().trim());
                updatedUser.setPhone(Objects.requireNonNull(binding.txtPhone.getText()).toString().trim());
                updatedUser.setPhoto(userImageBase64);

                updatedUser.setAddress(new Address());
                updatedUser.getAddress().setCountry(Objects.requireNonNull(binding.txtCountry.getText()).toString().trim());
                updatedUser.getAddress().setCity(Objects.requireNonNull(binding.txtCity.getText()).toString().trim());
                updatedUser.getAddress().setStreetName(Objects.requireNonNull(binding.txtStreet.getText()).toString().trim());
                updatedUser.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtStreetNumber.getText()).toString().trim());

                String newPassword = Objects.requireNonNull(binding.txtPasswordNew.getText()).toString().trim();
                if (!newPassword.isEmpty()) {
                    updatedUser.setNewPassword(newPassword);
                }
                UpdateCompanyRequest company = new UpdateCompanyRequest();
                company.setId(user.getCompany().getId());
                company.setName(Objects.requireNonNull(binding.txtCompanyName.getText()).toString());
                company.getAddress().setCountry(Objects.requireNonNull(binding.txtCountry.getText()).toString());
                company.getAddress().setCity(Objects.requireNonNull(binding.txtCompanyCity.getText()).toString());
                company.getAddress().setStreetName(Objects.requireNonNull(binding.txtCompanyStreet.getText()).toString());
                company.getAddress().setStreetNumber(Objects.requireNonNull(binding.txtCompanyStreetNumber.getText()).toString());
                company.setPhone(Objects.requireNonNull(binding.txtCompanyPhone.getText()).toString());
                company.setDescription(Objects.requireNonNull(binding.txtDescription.getText()).toString());
                company.setPhotos(companyImagesBase64);
                updatedUser.setCompany(company);
                viewModel.update(updatedUser);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                ToastUtils.showCustomToast(requireContext(),
                        success,
                        false);

                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                ToastUtils.showCustomToast(requireContext(),
                        error,
                        false);
            }
        });
    }
    private List<ValidationField> getUserValidationFields() {
        return Arrays.asList(
                new ValidationField(binding.txtFieldNewPassword, binding.txtPasswordNew, getString(R.string.new_password),
                        new OptionalMinLengthRule(8)),
                new ValidationField(binding.txtFieldOldPassword, binding.txtPasswordOld, getString(R.string.old_password),
                        new OptionalMinLengthRule(8), new RequiredOldPasswordRule(binding.txtPasswordNew)),
                new ValidationField(binding.txtFieldConfirmNewPassword, binding.txtConfirmPassword, getString(R.string.confirm_new_password),
                        new MatchPasswordRule(binding.txtPasswordNew)),
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
        return validateFields(getUserValidationFields()) && validateFields(getCompanyValidationFields());
    }

    @Override
    public void onImageSelected(String base64Image, Bitmap bitmap) {
        if (isUserImageSelection) {
            userImageBase64 = base64Image;
            updateUserImageUI();
        } else {
            companyImagesBase64.add(base64Image);
            updateCompanyImagesUI();
        }
    }

    @Override
    public void onImageError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStop() {
        super.onStop();
        user = null;
        userImageBase64 = null;
        companyImagesBase64 = null;
    }
}
