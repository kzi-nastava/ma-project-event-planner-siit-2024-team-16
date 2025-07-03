package com.example.evenmate.fragments.event;


import static com.example.evenmate.adapters.LocalDateTypeAdapter.DATE_FORMAT;
import static com.example.evenmate.adapters.LocalDateTypeAdapter.FORMATTER;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.databinding.FragmentEventFormBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.event.EventType;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import lombok.val;

public class EventFormFragment extends DialogFragment {
    private FragmentEventFormBinding binding;
    private EventFormViewModel viewModel;
    private EventType selectedEventType = null;
    private Event event;
    private Spinner spinner;
    private ArrayAdapter<EventType> spinnerAdapter;

    public EventFormFragment() {
        this.event = null;
    }

    public EventFormFragment(Event event) {
        this.event = event;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (event != null) {
                selectedEventType = event.getType();
            }
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentEventFormBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(EventFormViewModel.class);

        setupSaveButton();
        setupFormFields();
        setupDatePicker();
        setupSpinner();
        observeViewModel();
        String title = event == null ? "Add Event " : "Edit Event ";

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupSpinner() {
        spinner = binding.spinnerType;
        spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEventType = spinnerAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEventType = null;
            }
        });
    }

    private void setupDatePicker() {
        binding.showDatePickerButton.setOnClickListener(v ->{
            showDatePicker();
        });
    }


    private void setupSaveButton() {
        binding.btnSaveEvent.setOnClickListener(v -> {
            if (validateInput()) {
                getEventValues();
                if (event == null) {
                    viewModel.addEvent(event);
                } else {
                    viewModel.updateEvent(event);
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        clearAllErrors();
        String name = Objects.requireNonNull(binding.eName.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.eDescription.getText()).toString().trim();
        String country = Objects.requireNonNull(binding.eCountry.getText()).toString().trim();
        String city = Objects.requireNonNull(binding.eCity.getText()).toString().trim();
        String street = Objects.requireNonNull(binding.eStreet.getText()).toString().trim();
        String streetNumber = Objects.requireNonNull(binding.eStreetNumber.getText()).toString().trim();
        String maxAttendees = Objects.requireNonNull(binding.eMaxAttendees.getText()).toString().trim();
        String date = Objects.requireNonNull(binding.eDate.getText()).toString().trim();
        //TODO IMAGE VALIDATION

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

        if (TextUtils.isEmpty(country)) {
            binding.tilEventCountry.setError("Country is required");
            isValid = false;
        } else {
            binding.tilEventCountry.setError(null);
        }

        if (TextUtils.isEmpty(city)) {
            binding.tilEventCity.setError("City is required");
            isValid = false;
        } else {
            binding.tilEventCity.setError(null);
        }

        if (TextUtils.isEmpty(street)) {
            binding.tilEventStreet.setError("Street is required");
            isValid = false;
        } else {
            binding.tilEventStreet.setError(null);
        }

        if (TextUtils.isEmpty(streetNumber)) {
            binding.tilEventStreetNumber.setError("Street number is required");
            isValid = false;
        } else {
            binding.tilEventStreetNumber.setError(null);
        }

        if (TextUtils.isEmpty(maxAttendees)) {
            binding.tilEventMaxAttendees.setError("Max attendees number is required");
            isValid = false;
        } else {
            try {
                int maxAttendeesInt = Integer.parseInt(maxAttendees);
                if (maxAttendeesInt <= 0) {
                    binding.tilEventMaxAttendees.setError("Max attendees must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.tilEventMaxAttendees.setError("Max attendees must be a valid number");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(date)) {
            binding.tilEventDate.setError("Date is required. Please choose one");
            isValid = false;
        } else {
            binding.tilEventDate.setError(null);
        }

        return isValid;
    }
    private void clearAllErrors() {
        binding.tilEventName.setError(null);
        binding.tilEventDescription.setError(null);
        binding.tilEventCountry.setError(null);
        binding.tilEventCity.setError(null);
        binding.tilEventStreet.setError(null);
        binding.tilEventStreetNumber.setError(null);
        binding.tilEventMaxAttendees.setError(null);
        binding.tilEventDate.setError(null);
    }

    private String getTextFromField(android.widget.EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }
    private void getEventValues() {
        String name = getTextFromField(binding.eName);
        String description = getTextFromField(binding.eDescription);
        String country = getTextFromField(binding.eCountry);
        String city = getTextFromField(binding.eCity);
        String street = getTextFromField(binding.eStreet);
        String streetNumber = getTextFromField(binding.eStreetNumber);
        String maxAttendees = getTextFromField(binding.eMaxAttendees);
        String date = getTextFromField(binding.eDate);

        if(event == null) {
            event = new Event();
        }
        event.setDescription(description);
        event.setName(name);
        event.setAddress(new Address(street, streetNumber, city, country));
        event.setMaxAttendees(Integer.valueOf(maxAttendees));
        event.setDate(LocalDate.parse(date, FORMATTER));
        event.setType(selectedEventType);
//        event.setPhoto(name);
    }

    private void setupFormFields() {
        if(event != null){
            binding.eName.setText(event.getName());
            binding.eName.setEnabled(false);
            binding.eDescription.setText(event.getDescription());
            binding.eCountry.setText(event.getAddress().getCountry());
            binding.eCity.setText(event.getAddress().getCity());
            binding.eStreet.setText(event.getAddress().getStreetName());
            binding.eStreetNumber.setText(event.getAddress().getStreetNumber());
            binding.eMaxAttendees.setText(event.getMaxAttendees());
            binding.eDate.setText(event.getDate().format(FORMATTER));
//            binding.spinnerType.set
        }
    }
    private void observeViewModel() {
        viewModel.getTypes().observe(this, types -> {
            Log.d("Types", "Received types: " + types.size());
            spinnerAdapter.clear();
            spinnerAdapter.addAll(types);
            spinnerAdapter.notifyDataSetChanged();

            // Set selected type if in edit mode
            if (event != null && selectedEventType != null) {
                int position = types.indexOf(selectedEventType);
                if (position >= 0) {
                    spinner.setSelection(position);
                }
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

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
    private void showDatePicker() {
        val constraintsBuilder =
                new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now());

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select event date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build();


        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            binding.eDate.setText(sdf.format(date));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }
}