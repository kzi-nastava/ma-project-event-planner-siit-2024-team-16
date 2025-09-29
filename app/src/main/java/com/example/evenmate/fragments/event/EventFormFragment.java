package com.example.evenmate.fragments.event;

import static com.example.evenmate.adapters.LocalDateTypeAdapter.DATE_FORMAT;
import static com.example.evenmate.adapters.LocalDateTypeAdapter.FORMATTER;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evenmate.databinding.FragmentEventFormBinding;
import com.example.evenmate.models.Address;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.event.EventRequest;
import com.example.evenmate.utils.ImageUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import lombok.val;

public class EventFormFragment extends DialogFragment implements ImageUtils.ImageHandlerCallback {
    private FragmentEventFormBinding binding;
    private EventFormViewModel viewModel;
    private Long selectedEventTypeId = (long) -1;
    private EventRequest event;
    private String title;
    private ImageUtils imageHandler;
    private String eventImageBase64;

    public EventFormFragment() {
    }

    public static EventFormFragment newInstance(Event event) {
        EventFormFragment fragment = new EventFormFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageHandler = new ImageUtils(this, this);

        if (getArguments() != null) {
            Event receivedEvent = getArguments().getParcelable("event");
            if (receivedEvent != null) {
                event = receivedEvent.toRequest();
                if (event != null) {
                    selectedEventTypeId = event.getTypeId();
                    eventImageBase64 = event.getPhoto();
                }
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentEventFormBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(EventFormViewModel.class);
        viewModel.resetState();
        setupNextButton();
        setupFormFields();
        setupDatePicker();
        setupImageUpload();
        observeViewModel();
        title = event == null ? "Add Event" : "Edit Event";

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(binding.getRoot())
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    private void setupImageUpload() {
        binding.iconUploadEvent.setOnClickListener(v -> imageHandler.selectImage());

        if (eventImageBase64 != null && !eventImageBase64.isEmpty()) {
            ImageUtils.setImageFromBase64(binding.iconUploadEvent, eventImageBase64);
        }
    }

    private void setupDatePicker() {
        binding.showDatePickerButton.setOnClickListener(v -> showDatePicker());
    }

    private void setupNextButton() {
        binding.btnNext.setOnClickListener(v -> {
            if (validateInput()) {
                getEventValues();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                bundle.putString("title", title);
//                 FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                dismiss();

                EventTypeGroup nextDialog = new EventTypeGroup();
                nextDialog.setArguments(bundle);
//                 nextDialog.show(fragmentManager, "eventTypeGroup");
                nextDialog.show(getParentFragmentManager(), "eventTypeGroup");
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

         if (eventImageBase64 == null || eventImageBase64.isEmpty()) {
             Toast.makeText(requireContext(), "Please select a photo", Toast.LENGTH_SHORT).show();
             isValid = false;
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
        Boolean isPrivate = binding.switchPrivate.isChecked();

        if (event == null) {
            event = new EventRequest();
        }
        event.setDescription(description);
        event.setName(name);
        event.setAddress(new Address(street, streetNumber, city, country));
        event.setMaxAttendees(Integer.valueOf(maxAttendees));
        event.setDate(LocalDate.parse(date, FORMATTER));
        event.setTypeId(selectedEventTypeId);
        event.setIsPrivate(isPrivate);
        event.setPhoto(eventImageBase64);
    }

    private void setupFormFields() {
        if (event != null) {
            binding.eName.setText(event.getName());
            binding.eName.setEnabled(false);
            binding.eDescription.setText(event.getDescription());
            binding.eCountry.setText(event.getAddress().getCountry());
            binding.eCity.setText(event.getAddress().getCity());
            binding.eStreet.setText(event.getAddress().getStreetName());
            binding.eStreetNumber.setText(event.getAddress().getStreetNumber());
            binding.eMaxAttendees.setText(Integer.toString(event.getMaxAttendees()));
            binding.eDate.setText(event.getDate().format(FORMATTER));
            binding.switchPrivate.setChecked(event.getIsPrivate());

            if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
                eventImageBase64 = event.getPhoto();
                ImageUtils.setImageFromBase64(binding.iconUploadEvent, eventImageBase64);
            }
        }
    }

    private void observeViewModel() {
        viewModel.getSuccess().observe(this, success -> {
            if (success != null) {
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

    @Override
    public void onImageSelected(String base64Image, Bitmap bitmap) {
        eventImageBase64 = base64Image;
        binding.iconUploadEvent.setImageBitmap(bitmap);
    }

    @Override
    public void onImageError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        event = null;
        eventImageBase64 = null;
    }
}