package com.example.evenmate.fragments.profile;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.databinding.FragmentProfileBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.user.Block;
import com.example.evenmate.models.user.Company;
import com.example.evenmate.models.user.Report;
import com.example.evenmate.models.user.User;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        setupCompanyImagesRecycler();

        if (getArguments() != null && getArguments().containsKey("userId")) {
            String userIdStr = getArguments().getString("userId");
            Long userId = userIdStr != null ? Long.parseLong(userIdStr) : null;
            if (userId != null && !userId.equals(AuthManager.loggedInUser.getId())) {
                ClientUtils.userService.getUserById(userId).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        user = response.body();
                        if (user != null) {
                            setupClickListeners();
                            updateUI();
                            showProperButtons();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        ToastUtils.showCustomToast(requireContext(), "Cannot load user", true);
                    }
                });
                return;
            }
        }

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.setUser(AuthManager.loggedInUser);
        user = viewModel.getUser().getValue();
        setupClickListeners();
        updateUI();
        showProperButtons();
    }

    private void showProperButtons() {
        if (user.getId().equals(AuthManager.loggedInUser.getId())) {
            binding.btnEditProfile.setVisibility(View.VISIBLE);
            binding.btnDeleteUser.setVisibility(View.VISIBLE);
            binding.btnReportUser.setVisibility(View.GONE);
            binding.btnBlockUser.setVisibility(View.GONE);
        } else {
            binding.btnEditProfile.setVisibility(View.GONE);
            binding.btnDeleteUser.setVisibility(View.GONE);
            binding.btnReportUser.setVisibility(View.VISIBLE);
            binding.btnBlockUser.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        ImageButton editProfileButton = binding.btnEditProfile;
        ImageButton reportButton = binding.btnReportUser;
        ImageButton blockButton = binding.btnBlockUser;

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

        reportButton.setOnClickListener(v -> {
            final View inputView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_report, null);
            final EditText reasonInput = inputView.findViewById(R.id.editTextReason);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Report User")
                    .setView(inputView)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Report", (dialog, which) -> {
                        String reportReason = reasonInput.getText().toString().trim();
                        if (reportReason.isEmpty()) {
                            ToastUtils.showCustomToast(requireContext(), "Please enter a reason", true);
                            return;
                        }
                        ClientUtils.userService.reportUser(user.getId(), reportReason)
                                .enqueue(new Callback<Report>() {
                                    @Override
                                    public void onResponse(Call<Report> call, Response<Report> response) {
                                        if (response.isSuccessful()) {
                                            ToastUtils.showCustomToast(requireContext(), "User reported", false);
                                        } else {
                                            ToastUtils.showCustomToast(requireContext(), "Failed to report user", true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Report> call, Throwable t) {
                                        ToastUtils.showCustomToast(requireContext(), "Error reporting user", true);
                                    }
                                });
                    })
                    .show();
        });

        ClientUtils.userService.isBlocked(user.getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean blocked = response.body();
                    updateBlockButton(blocked);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                ToastUtils.showCustomToast(requireContext(), "Error checking block status", true);
            }
        });

        blockButton.setOnClickListener(v -> {
            ClientUtils.userService.blockUser(user.getId())
                    .enqueue(new Callback<Block>() {
                        @Override
                        public void onResponse(Call<Block> call, Response<Block> response) {
                           checkBlockStatus();
                        }

                        @Override
                        public void onFailure(Call<Block> call, Throwable t) {
                            checkBlockStatus();
                        }
                    });
        });

    }

    private void checkBlockStatus() {
        ClientUtils.userService.isBlocked(user.getId())
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean blocked = response.body();
                            updateBlockButton(blocked);
                            String message = blocked ? "User blocked" : "User unblocked";
                            ToastUtils.showCustomToast(requireContext(), message, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        ToastUtils.showCustomToast(requireContext(), "Error refreshing block status", true);
                    }
                });
    }

    private void updateBlockButton(boolean blocked) {
        binding.btnBlockUser.setColorFilter(getResources().getColor(blocked ? R.color.red : R.color.black));
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
        String base64Image = user.getPhoto();
        if (base64Image.contains(",")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        }
        Glide.with(requireContext())
                .asBitmap()
                .load(Base64.decode(base64Image, Base64.DEFAULT))
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