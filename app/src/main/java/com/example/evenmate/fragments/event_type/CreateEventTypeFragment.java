package com.example.evenmate.fragments.event_type;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.evenmate.databinding.FragmentCreateEventTypeBinding;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.EventType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

public class CreateEventTypeFragment extends DialogFragment {
    private FragmentCreateEventTypeBinding binding;
    private CreateEventTypeViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEventTypeBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(CreateEventTypeViewModel.class);

        setupCategoriesSpinner();
        setupSaveButton();
        observeViewModel();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Event Type")
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupCategoriesSpinner() {
        //TODO: fix the problem when saving the type
        List<Category> categories = List.of(new Category("food"), new Category("balloons"));
        binding.multiSelectSpinner.setItems(categories);
    }

    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(requireContext(), "Action successful", Toast.LENGTH_SHORT).show();
                dismissAllowingStateLoss();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                EventType newEventType = createEventType();
                viewModel.addEventType(newEventType);
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.etDescription.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError("Name is required");
            isValid = false;
        } else {
            binding.tilName.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            binding.tilDescription.setError("Description is required");
            isValid = false;
        } else {
            binding.tilDescription.setError(null);
        }

        return isValid;
    }

    private EventType createEventType() {
        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.etDescription.getText()).toString().trim();
        List<Category> selectedCategories = binding.multiSelectSpinner.getSelectedItems();

        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setRecommendedCategories(selectedCategories);

        return eventType;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}