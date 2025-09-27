package com.example.evenmate.fragments.service;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.R;
import com.example.evenmate.databinding.FragmentAddServiceBinding;
import com.example.evenmate.models.category.Category;
import com.example.evenmate.models.service.ServiceCreate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddServiceFragment extends Fragment {
    private FragmentAddServiceBinding binding;
    private ServiceFormViewModel viewModel;
    private List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddServiceBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ServiceFormViewModel.class);
        setupObservers();
        viewModel.fetchCategories();
        setupUI();
        return binding.getRoot();
    }

    private void setupObservers() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryList = categories;
            List<String> categoryNames = new ArrayList<>();
            categoryNames.add(getString(R.string.select_category)); // Default value
            for (Category c : categories) categoryNames.add(c.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.categoryDropdown.setAdapter(adapter);
            binding.categoryDropdown.setSelection(0);
        });
        viewModel.getSuccess().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupUI() {
        binding.suggestCategoryButton.setOnClickListener(v -> binding.newCategoryLayout.setVisibility(View.VISIBLE));
        binding.createButton.setOnClickListener(v -> submitForm());
        binding.durationTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixedDurationRadio) {
                binding.fixedDurationLayout.setVisibility(View.VISIBLE);
                binding.rangeDurationLayout.setVisibility(View.GONE);
            } else {
                binding.fixedDurationLayout.setVisibility(View.GONE);
                binding.rangeDurationLayout.setVisibility(View.VISIBLE);
            }
        });
        binding.categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCategory = null;
                } else {
                    selectedCategory = categoryList.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });
        binding.reservationTypeRadioGroup.check(R.id.automaticRadio);
    }

    private void submitForm() {
        if (!validateInput()) {
            return;
        }

        ServiceCreate service = new ServiceCreate();

        service.setName(binding.nameInput.getText().toString().trim());
        service.setDescription(binding.descriptionInput.getText().toString().trim());
        service.setPrice(Double.parseDouble(binding.priceInput.getText().toString()));
        service.setDiscount(Integer.parseInt(binding.discountInput.getText().toString()));
        service.setDistinctiveness(binding.distinctivenessInput.getText().toString().trim());
        service.setReservationDeadline(Integer.parseInt(binding.reservationDeadlineInput.getText().toString()));
        service.setCancellationDeadline(Integer.parseInt(binding.cancellationDeadlineInput.getText().toString()));
        service.setIsVisible(binding.visibilitySwitch.isChecked());
        service.setIsAvailable(binding.availabilitySwitch.isChecked());
        service.setCategoryId(selectedCategory != null ? selectedCategory.getId() : null);
        service.setNewCategoryName(!TextUtils.isEmpty(binding.newCategoryInput.getText()) ? binding.newCategoryInput.toString().trim() : null);
        service.setNewCategoryDescription(!TextUtils.isEmpty(binding.categoryDescriptionInput.getText()) ? binding.categoryDescriptionInput.toString().trim() : null);
        service.setReservationConfirmation(binding.reservationTypeRadioGroup.getCheckedRadioButtonId() == R.id.automaticRadio ? "Automatic" : "Manual");
        if (binding.fixedDurationRadio.isChecked()) {
            int hours = Integer.parseInt(binding.fixedHoursInput.getText().toString());
            int minutes = Integer.parseInt(binding.fixedMinutesInput.getText().toString());
            service.setLength(hours*60 + minutes);
        } else {
            int minHours = Integer.parseInt(binding.minHoursInput.getText().toString());
            int minMinutes = Integer.parseInt(binding.minMinutesInput.getText().toString());
            int maxHours = Integer.parseInt(binding.maxHoursInput.getText().toString());
            int maxMinutes = Integer.parseInt(binding.maxMinutesInput.getText().toString());
            service.setMinLength(minHours*60 + minMinutes);
            service.setMaxLength(maxHours*60 + maxMinutes);
        }

        viewModel.addService(service);
    }

    private boolean validateInput() {
        boolean isValid = true;

        clearErrors();
        String name = Objects.requireNonNull(binding.nameInput.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.descriptionInput.getText()).toString().trim();
        String price = Objects.requireNonNull(binding.priceInput.getText()).toString().trim();
        String discount = Objects.requireNonNull(binding.discountInput.getText()).toString().trim();
        String distinctiveness = Objects.requireNonNull(binding.distinctivenessInput.getText()).toString().trim();
        String reservationDeadline = Objects.requireNonNull(binding.reservationDeadlineInput.getText()).toString().trim();
        String cancellationDeadline = Objects.requireNonNull(binding.cancellationDeadlineInput.getText()).toString().trim();
        String lengthHours = Objects.requireNonNull(binding.fixedHoursInput.getText()).toString().trim();
        String lengthMinutes = Objects.requireNonNull(binding.fixedMinutesInput.getText()).toString().trim();
        String minLengthHours = Objects.requireNonNull(binding.minHoursInput.getText()).toString().trim();
        String minLengthMinutes = Objects.requireNonNull(binding.minMinutesInput.getText()).toString().trim();
        String maxLengthHours = Objects.requireNonNull(binding.maxHoursInput.getText()).toString().trim();
        String maxLengthMinutes = Objects.requireNonNull(binding.maxMinutesInput.getText()).toString().trim();

        String newCategoryName = Objects.requireNonNull(binding.newCategoryInput.getText()).toString().trim();
        String newCategoryDescription = Objects.requireNonNull(binding.categoryDescriptionInput.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.nameInput.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            binding.nameInput.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            binding.descriptionInput.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            binding.descriptionInput.setError(null);
        }

        if (TextUtils.isEmpty(distinctiveness)) {
            binding.distinctivenessInput.setError(getString(R.string.required_field));
            isValid = false;
        } else {
            binding.distinctivenessInput.setError(null);
        }

        if (selectedCategory == null) {
            if (TextUtils.isEmpty(newCategoryName)) {
                binding.newCategoryInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                binding.newCategoryInput.setError(null);
            }
            if (TextUtils.isEmpty(newCategoryDescription)) {
                binding.categoryDescriptionInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                binding.categoryDescriptionInput.setError(null);
            }
        } else {
            if (!TextUtils.isEmpty(newCategoryName)) {
                binding.newCategoryInput.setError("Either select an existing category or provide details for a new one, not both.");
                isValid = false;
            } else {
                binding.newCategoryInput.setError(null);
            }
            if (!TextUtils.isEmpty(newCategoryDescription)) {
                binding.categoryDescriptionInput.setError("Either select an existing category or provide details for a new one, not both.");
                isValid = false;
            } else {
                binding.categoryDescriptionInput.setError(null);
            }

            if (TextUtils.isEmpty(price)) {
                binding.priceInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                try {
                    double priceDouble = Double.parseDouble(price);
                    if (priceDouble <= 0) {
                        binding.priceInput.setError("Price must be greater than 0");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    binding.priceInput.setError("Price must be a valid number");
                    isValid = false;
                }
            }
            if (TextUtils.isEmpty(discount)) {
                binding.discountInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                try {
                    int discountDouble = Integer.parseInt(discount);
                    if (discountDouble <= 0) {
                        binding.discountInput.setError("Discount must be greater than 0");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    binding.discountInput.setError("Discount must be a valid number");
                    isValid = false;
                }
            }

            if (TextUtils.isEmpty(reservationDeadline)) {
                binding.reservationDeadlineInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                try {
                    int reservationDeadlineInt = Integer.parseInt(reservationDeadline);
                    if (reservationDeadlineInt <= 0) {
                        binding.reservationDeadlineInput.setError("Reservation deadline must be greater than 0");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    binding.reservationDeadlineInput.setError("Reservation deadline must be a valid number");
                    isValid = false;
                }
            }

            if (TextUtils.isEmpty(cancellationDeadline)) {
                binding.cancellationDeadlineInput.setError(getString(R.string.required_field));
                isValid = false;
            } else {
                try {
                    int cancellationDeadlineInt = Integer.parseInt(cancellationDeadline);
                    if (cancellationDeadlineInt <= 0) {
                        binding.cancellationDeadlineInput.setError("Cancellation deadline must be greater than 0");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    binding.cancellationDeadlineInput.setError("Cancellation deadline must be a valid number");
                    isValid = false;
                }
            }

            if (binding.fixedDurationRadio.isChecked()) {
                if (TextUtils.isEmpty(lengthHours) && TextUtils.isEmpty(lengthMinutes)) {
                    binding.fixedHoursInput.setError(getString(R.string.required_field));
                    binding.fixedMinutesInput.setError(getString(R.string.required_field));
                    isValid = false;
                } else {
                    int hours = 0;
                    int minutes = 0;
                    try {
                        hours = TextUtils.isEmpty(lengthHours) ? 0 : Integer.parseInt(lengthHours);
                        binding.fixedHoursInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.fixedHoursInput.setError("Hours must be a valid number");
                        isValid = false;
                    }
                    try {
                        minutes = TextUtils.isEmpty(lengthMinutes) ? 0 : Integer.parseInt(lengthMinutes);
                        binding.fixedMinutesInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.fixedMinutesInput.setError("Minutes must be a valid number");
                        isValid = false;
                    }
                    if (hours == 0 && minutes == 0) {
                        binding.fixedHoursInput.setError("Length cannot be zero");
                        binding.fixedMinutesInput.setError("Length cannot be zero");
                        isValid = false;
                    }
                }
            } else {
                if ((TextUtils.isEmpty(minLengthHours) && TextUtils.isEmpty(minLengthMinutes)) ||
                        (TextUtils.isEmpty(maxLengthHours) && TextUtils.isEmpty(maxLengthMinutes))) {
                    if (TextUtils.isEmpty(minLengthHours) && TextUtils.isEmpty(minLengthMinutes)) {
                        binding.minHoursInput.setError(getString(R.string.required_field));
                        binding.minMinutesInput.setError(getString(R.string.required_field));
                    }
                    if (TextUtils.isEmpty(maxLengthHours) && TextUtils.isEmpty(maxLengthMinutes)) {
                        binding.maxHoursInput.setError(getString(R.string.required_field));
                        binding.maxMinutesInput.setError(getString(R.string.required_field));
                    }
                    isValid = false;
                } else {
                    int minHours = 0, minMinutes = 0, maxHours = 0, maxMinutes = 0;
                    try {
                        minHours = TextUtils.isEmpty(minLengthHours) ? 0 : Integer.parseInt(minLengthHours);
                        binding.minHoursInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.minHoursInput.setError("Hours must be a valid number");
                        isValid = false;
                    }
                    try {
                        minMinutes = TextUtils.isEmpty(minLengthMinutes) ? 0 : Integer.parseInt(minLengthMinutes);
                        binding.minMinutesInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.minMinutesInput.setError("Minutes must be a valid number");
                        isValid = false;
                    }
                    try {
                        maxHours = TextUtils.isEmpty(maxLengthHours) ? 0 : Integer.parseInt(maxLengthHours);
                        binding.maxHoursInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.maxHoursInput.setError("Hours must be a valid number");
                        isValid = false;
                    }
                    try {
                        maxMinutes = TextUtils.isEmpty(maxLengthMinutes) ? 0 : Integer.parseInt(maxLengthMinutes);
                        binding.maxMinutesInput.setError(null);
                    } catch (NumberFormatException e) {
                        binding.maxMinutesInput.setError("Minutes must be a valid number");
                        isValid = false;
                    }
                    if (minHours == 0 && minMinutes == 0) {
                        binding.minHoursInput.setError("Minimum length cannot be zero");
                        binding.minMinutesInput.setError("Minimum length cannot be zero");
                        isValid = false;
                    }
                    if (maxHours == 0 && maxMinutes == 0) {
                        binding.maxHoursInput.setError("Maximum length cannot be zero");
                        binding.maxMinutesInput.setError("Maximum length cannot be zero");
                        isValid = false;
                    }
                    if (isValid) {
                        int totalMinMinutes = minHours * 60 + minMinutes;
                        int totalMaxMinutes = maxHours * 60 + maxMinutes;
                        if (totalMinMinutes >= totalMaxMinutes) {
                            binding.minHoursInput.setError("Minimum length must be less than maximum length");
                            binding.minMinutesInput.setError("Minimum length must be less than maximum length");
                            binding.maxHoursInput.setError("Maximum length must be greater than minimum length");
                            binding.maxMinutesInput.setError("Maximum length must be greater than minimum length");
                            isValid = false;
                        }
                    }
                }
            }
        }
        return isValid;
    }

    private void clearErrors() {
        binding.nameInput.setError(null);
        binding.descriptionInput.setError(null);
        binding.priceInput.setError(null);
        binding.discountInput.setError(null);
        binding.distinctivenessInput.setError(null);
        binding.reservationDeadlineInput.setError(null);
        binding.cancellationDeadlineInput.setError(null);
        binding.fixedHoursInput.setError(null);
        binding.fixedMinutesInput.setError(null);
        binding.minHoursInput.setError(null);
        binding.minMinutesInput.setError(null);
        binding.maxHoursInput.setError(null);
        binding.maxMinutesInput.setError(null);
        binding.newCategoryInput.setError(null);
        binding.categoryDescriptionInput.setError(null);
    }
}
