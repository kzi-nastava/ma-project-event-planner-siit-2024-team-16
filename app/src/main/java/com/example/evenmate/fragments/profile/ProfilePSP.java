package com.example.evenmate.fragments.profile;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.adapters.ProfileAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentProfilePSPBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.models.user.User;

public class ProfilePSP extends Fragment{

    private final User user = AuthManager.loggedInUser;
    private ProfileViewModel viewModel;
    private ProfileAdapter imagesAdapter;

    // Views
    private ImageView profileImage;
    private TextView nameText, emailText, phoneText, addressText;
    private TextView companyNameText, companyEmailText, companyPhoneText,
            companyAddressText, companyDescriptionText;
    private RecyclerView companyImagesRecycler;
    private View companyCard;
    private Button editProfileButton, changePhotoButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentProfilePSPBinding binding = FragmentProfilePSPBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        initViews(view);
        setupCompanyImagesRecycler();
        setupClickListeners();
        updateUI(user);
    }

    private void initViews(View view) {
        // Profile views
        profileImage = view.findViewById(R.id.psp_profile_image);
        nameText = view.findViewById(R.id.name);
        emailText = view.findViewById(R.id.email);
        phoneText = view.findViewById(R.id.phone);
        addressText = view.findViewById(R.id.address);

        // Company views
        companyNameText = view.findViewById(R.id.company_name);
        companyEmailText = view.findViewById(R.id.company_email);
        companyPhoneText = view.findViewById(R.id.company_phone);
        companyAddressText = view.findViewById(R.id.company_address);
        companyDescriptionText = view.findViewById(R.id.company_description);
        companyImagesRecycler = view.findViewById(R.id.company_images_recycler);

        // Buttons
//        editProfileButton = view.findViewById(R.id.edit_profile_button);
//        changePhotoButton = view.findViewById(R.id.change_photo_button);

        // Other views
        View profileCard = view.findViewById(R.id.profile_card);
        companyCard = view.findViewById(R.id.company_card);
    }


    private void setupClickListeners() {
        // Profile image click to change photo
//        profileImage.setOnClickListener(v -> imageUtils.selectImage());

        // Change photo button
//        if (changePhotoButton != null) {
//            changePhotoButton.setOnClickListener(v -> imageUtils.selectImage());
//        }

        // Edit profile button
//        if (editProfileButton != null) {
//            editProfileButton.setOnClickListener(v -> openEditProfile());
//        }
    }

    private void setupCompanyImagesRecycler() {
        imagesAdapter = new ProfileAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        companyImagesRecycler.setLayoutManager(layoutManager);
        companyImagesRecycler.setAdapter(imagesAdapter);
    }

    private void updateUI(User user) {
        if (user == null) return;

        // Update profile information
        updateProfileSection(user);

        // Update company information
        if (user.getCompany() != null) {
            updateCompanySection(user.getCompany());
            companyCard.setVisibility(View.VISIBLE);
        } else {
            companyCard.setVisibility(View.GONE);
        }
    }

      private void updateProfileSection(User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        nameText.setText(!fullName.trim().isEmpty() ? fullName : "N/A");
        emailText.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        phoneText.setText(user.getPhone() != null ? user.getPhone() : "N/A");
        if (user.getAddress() != null) {
            addressText.setText(formatAddress(user.getAddress()));
        } else {
            addressText.setText("N/A");
        }
        if(user.getPhoto() != null){
            Glide.with(requireContext())
                    .load(Base64.decode(user.getPhoto(), Base64.DEFAULT))
                    .into(profileImage);
        }
    }

    private void updateCompanySection(Company company) {
        companyNameText.setText(company.getName() != null ? company.getName() : "N/A");
        companyEmailText.setText(company.getEmail() != null ? company.getEmail() : "N/A");
        companyPhoneText.setText(company.getPhone() != null ? company.getPhone() : "N/A");

        if (company.getAddress() != null) {
            companyAddressText.setText(formatAddress(company.getAddress()));
        } else {
            companyAddressText.setText("N/A");
        }

        companyDescriptionText.setText(company.getDescription() != null ? company.getDescription() : "N/A");

        if (company.getPhotos() != null && !company.getPhotos().isEmpty()) {
            imagesAdapter.setImages(company.getPhotos());
            companyImagesRecycler.setVisibility(View.VISIBLE);
        } else {
            companyImagesRecycler.setVisibility(View.GONE);
        }
    }

    private String formatAddress(Address address) {
        StringBuilder sb = new StringBuilder();

        if (address.getStreetName() != null) sb.append(address.getStreetName()).append(" ");
        if (address.getStreetNumber() != null) sb.append(address.getStreetNumber()).append(", ");
        if (address.getCity() != null) sb.append(address.getCity()).append(", ");
        if (address.getCountry() != null) sb.append(address.getCountry());

        String result = sb.toString();
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }
}