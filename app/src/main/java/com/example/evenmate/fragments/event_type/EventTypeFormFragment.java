package com.example.evenmate.fragments.event_type;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.evenmate.databinding.FragmentEventTypeFormBinding;
import com.example.evenmate.models.Category;
import com.example.evenmate.models.event.EventType;
import com.example.evenmate.utils.ToastUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

public class EventTypeFormFragment extends DialogFragment {
    private FragmentEventTypeFormBinding binding;
    private EventTypeFormViewModel viewModel;

    private EventType eventType;

    public EventTypeFormFragment() {
        this.eventType = null;
    }

    public EventTypeFormFragment(EventType eventType) {
        this.eventType = eventType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentEventTypeFormBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(EventTypeFormViewModel.class);

        viewModel.resetState();
        setupCategoriesSpinner();
        setupSaveButton();
        setupFormFields();
        observeViewModel();
        String title = eventType == null ? "Add Event Type" : "Edit Event Type";

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupCategoriesSpinner() {
        viewModel.fetchCategories();
        viewModel.getCategories().observe(this, categories -> {
            Log.d("Categories", "Received categories: " + categories.size());
            binding.multiSelectSpinner.setItems(categories);
            if(eventType != null){
                binding.multiSelectSpinner.setPreselectedItems(eventType.getRecommendedCategories());
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupSaveButton() {
        if(eventType == null) {
            binding.btnSave.setOnClickListener(v -> {
                if (validateInput()) {
                    getEventTypeValues();
                    viewModel.addEventType(eventType.toRequest());
                }
            });
        } else{
            binding.btnSave.setOnClickListener(v -> {
                if (validateInput()) {
                    getEventTypeValues();
                    viewModel.updateEventType(eventType.toRequest());
                }
            });
        }
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

    private void getEventTypeValues() {
        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.etDescription.getText()).toString().trim();
        List<Category> selectedCategories = binding.multiSelectSpinner.getSelectedItems();

        if(eventType == null) {
            eventType = new EventType();
            eventType.setName(name);
        }
        eventType.setDescription(description);
        eventType.setRecommendedCategories(selectedCategories);
    }

    private void setupFormFields() {
        if(eventType != null){
            binding.etName.setText(eventType.getName());
            binding.etName.setEnabled(false);
            binding.etDescription.setText(eventType.getDescription());
        }
    }

    //todo fix refresh
    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (success) {
                ToastUtils.showCustomToast(requireContext(), "Action successful", false);
                sendResultAndDismiss();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastUtils.showCustomToast(requireContext(), error, true);
            }
        });
    }

    private void sendResultAndDismiss() {
        Bundle result = new Bundle();
        result.putBoolean("refresh_types", true);
        getParentFragmentManager().setFragmentResult("type_form_result", result);
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        eventType = null;
    }
}