package com.example.evenmate.fragments.event;


import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.databinding.FragmentEventFormBinding;
import com.example.evenmate.models.event.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class EventFormFragment extends DialogFragment {
    private FragmentEventFormBinding binding;
    private EventFormViewModel viewModel;

    private Event event;

    public EventFormFragment() {
        this.event = null;
    }

    public EventFormFragment(Event event) {
        this.event = event;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentEventFormBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(EventFormViewModel.class);

        setupSaveButton();
        setupFormFields();
        observeViewModel();
        String title = event == null ? "Add Event " : "Edit Event ";

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }


    private void setupSaveButton() {
        if(event == null) {
            binding.btnSaveEvent.setOnClickListener(v -> {
                if (validateInput()) {
                    getEventValues();
                    viewModel.addEvent(event);
                }
            });
        } else{
            binding.btnSaveEvent.setOnClickListener(v -> {
                if (validateInput()) {
                    getEventValues();
                    viewModel.updateEvent(event);
                }
            });
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        String name = Objects.requireNonNull(binding.eName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.eDescription.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.tilEventName.setError("Name is required");
            isValid = false;
        } else {
            binding.tilEventName.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            binding.tilEventDescription.setError("Description is required");
            isValid = false;
        } else {
            binding.tilEventDescription.setError(null);
        }

        return isValid;
    }

    private void getEventValues() {
        String description = Objects.requireNonNull(binding.eDescription.getText()).toString().trim();

        if(event == null) {
            event = new Event();
        }
        event.setDescription(description);
    }

    private void setupFormFields() {
        if(event != null){
            binding.eName.setText(event.getName());
            binding.eName.setEnabled(false);
            binding.eDescription.setText(event.getDescription());
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        event = null;
    }
//    private fun showDatePicker() {
//        val datePicker = MaterialDatePicker.Builder.datePicker()
//                .setTitleText("Select event date")
//                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                .build()
//
//        datePicker.addOnPositiveButtonClickListener { timeInMillis ->
//                // Format the date as needed
//                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                .format(timeInMillis)
//            // You can store the date or update UI here
//        }
//
//        datePicker.show(supportFragmentManager, "DATE_PICKER")
//    }
//
//// In your onCreate or wherever you setup views:
//binding.showDatePickerButton.setOnClickListener {
//        showDatePicker()
//    }
}