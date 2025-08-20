package com.example.evenmate.fragments.profile;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.evenmate.R;
import com.example.evenmate.adapters.ProfileAdapter;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.databinding.FragmentProfileBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment{

    private User user;
    private FragmentProfileBinding binding;
    private ProfileAdapter imagesAdapter;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.setUser(AuthManager.loggedInUser);
        user = viewModel.getUser().getValue();
        setupCompanyImagesRecycler();
        setupClickListeners();
        updateUI();

        viewModel.getDeleteFailed().observe(getViewLifecycleOwner(), failed -> {
            if (failed == null) return;

            if (failed) {
                ToastUtils.showCustomToast(requireContext(),
                        "Delete failed: user has future events",
                        true);
            } else {
                ToastUtils.showCustomToast(requireContext(),
                        String.format("%s successfully deleted",
                                user.getFirstName() + " " + user.getLastName()),
                        false);
                viewModel.resetDeleteFailed();
                AuthManager.loggedInUser = null;
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_profile_to_HomepageFragment);
            }
        });
    }

    private void setupClickListeners() {
        ImageButton editProfileButton = binding.btnEditProfile;
        editProfileButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_profile_to_editProfileFragment);
        });
        ImageButton deleteProfileButton = binding.btnDeleteUser;
        deleteProfileButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete User")
                .setMessage(String.format("Are you sure you want to delete %s? This action cannot be undone.", user.getFirstName() + " " + user.getLastName()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(user.getId()))
                .show()
            );
    }

    private void setupCompanyImagesRecycler() {
        imagesAdapter = new ProfileAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        binding.companyImagesRecycler.setLayoutManager(layoutManager);
        binding.companyImagesRecycler.setAdapter(imagesAdapter);
    }

    private void updateUI() {
        if (user == null) return;

        updateProfileSection();

        if (user.getCompany() != null) {
            updateCompanySection();
            binding.companyCard.setVisibility(View.VISIBLE);
        } else {
            binding.companyCard.setVisibility(View.GONE);
        }
    }

  private void updateProfileSection() {
    String fullName = user.getFirstName() + " " + user.getLastName();
    binding.name.setText(!fullName.trim().isEmpty() ? fullName : "N/A");
    binding.email.setText(user.getEmail() != null ? user.getEmail() : "N/A");
    binding.phone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
    if (user.getAddress() != null) {
        binding.address.setText(formatAddress(user.getAddress()));
    } else {
        binding.address.setText("N/A");
    }
    if(user.getPhoto() != null){
        Glide.with(requireContext())
                .load(Base64.decode(user.getPhoto(), Base64.DEFAULT))
                .into(binding.pspProfileImage);
        }
    }

    private void updateCompanySection() {
        Company company = user.getCompany();
        binding.companyName.setText(company.getName() != null ? company.getName() : "N/A");
        binding.companyEmail.setText(company.getEmail() != null ? company.getEmail() : "N/A");
        binding.companyPhone.setText(company.getPhone() != null ? company.getPhone() : "N/A");

        if (company.getAddress() != null) {
            binding.companyAddress.setText(formatAddress(company.getAddress()));
        } else {
            binding.companyAddress.setText("N/A");
        }

        binding.companyDescription.setText(company.getDescription() != null ? company.getDescription() : "N/A");

        if (company.getPhotos() != null && !company.getPhotos().isEmpty()) {
            imagesAdapter.setImages(company.getPhotos());
            binding.companyImagesRecycler.setVisibility(View.VISIBLE);
        } else {
            binding.companyImagesRecycler.setVisibility(View.GONE);
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