package com.example.evenmate.fragments.event_type;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.example.evenmate.databinding.FragmentCreateEventTypeBinding;
import com.example.evenmate.models.EventType;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateEventTypeFragment extends DialogFragment {
    private FragmentCreateEventTypeBinding binding;
    private ArrayAdapter<String> categoriesAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEventTypeBinding.inflate(getLayoutInflater());

        setupCategoriesSpinner();
        setupSaveButton();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Event Type")
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupCategoriesSpinner() {
        List<String> categories = Arrays.asList("ballons", "music", "food");

        // Set the items in your MultiSelectSpinner
        binding.spinnerCategories.setItems(categories);
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                EventType newEventType = createEventType();
                sendResult(newEventType);
                dismiss();
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
        List<String> selectedCategories = binding.spinnerCategories.getSelectedItems();

        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);

        List<String> recommendedCategories = new ArrayList<>();
        if (selectedCategories != null) {
            recommendedCategories.addAll(selectedCategories);
        }
        eventType.setRecommendedCategories(recommendedCategories);

        return eventType;
    }

    private void sendResult(EventType eventType) {
        EventTypesViewModel viewModel = new ViewModelProvider(requireActivity()).get(EventTypesViewModel.class);
        viewModel.addEventType(eventType);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}